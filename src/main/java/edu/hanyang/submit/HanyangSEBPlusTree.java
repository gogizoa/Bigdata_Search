package edu.hanyang.submit;

import java.io.*;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

import io.github.hyerica_bdml.indexer.BPlusTree;

public class HanyangSEBPlusTree implements BPlusTree {
    int blocksize;
    int height;
    int maxKeys;
    public int rootindex;
    int nblocks;
    int endindex;
    int fanout;
    String metafile;
    String treefile;
    RandomAccessFile raf;
    Stack<Integer> stack = new Stack<>();
    byte[] buf;
    ByteBuffer buffer;
    /**
     * B+ tree
     * @param metafile
     * @param treefile
     * @param blocksize
     * @param nblocks
     * @throws IOException
     */
    @Override
    public void open(String metafile, String treefile, int blocksize, int nblocks) throws IOException {
        if (Files.exists(Paths.get(metafile)))
        {
            DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(metafile)));
            this.rootindex = dos.readInt();
            this.endindex = dos.readInt();
            this.height = dos.readInt();
            dos.close();
        }
        else {
            this.rootindex = 0;
            this.endindex = 0;
            this.height = 0;
        }
           
        this.blocksize = blocksize;
        if(this.blocksize % 8 == 0) {
           this.blocksize += 4;
        }
        this.fanout = (this.blocksize/8) + 1;
        this.maxKeys = this.fanout;
        this.raf = new RandomAccessFile(treefile, "rw");
        this.metafile = metafile;
        this.treefile = treefile;
        this.buf = new byte[this.blocksize];
        this.buffer = ByteBuffer.wrap(this.buf);
        this.nblocks = nblocks;
    }
    /**
     * B+ tree
     * @param key
     * @param val
     * @throws IOException
     */
    @Override
    public void insert(int key, int val) throws IOException {
       stack.clear();
       buffer.clear();
       if(rootindex == 0 && endindex == 0) {
            Block first = new Block();
            endindex += blocksize;
            first.keys[0] = key;
            first.vals[0] = val;
            raf.seek(first.idx);
            int i;
            for(i = 0; i < fanout-1; i++) {
                buffer.putInt(first.keys[i]);
                buffer.putInt(first.vals[i]);
            }
            buffer.putInt(first.keys[i]);
            raf.write(buf);
            return;
       }
       Block b = searchNode(key);
       
       if (b.nvals >= fanout-1) {
           Block newnode = splitleaf(b, key, val);
           int left_idx = b.idx;
           newnode = new Block(newnode.idx);
           
           if(stack.empty()) {
               insertInternalleaf(left_idx, null, newnode);
           }
           else {
               Block parent = new Block(stack.pop());
               insertInternalleaf(left_idx, parent, newnode);
           }
       }
       
       else {
           int[] tmpkey = new int[fanout];
           int[] tmpval = new int[fanout-1];
           Arrays.fill(tmpkey,-1);
           Arrays.fill(tmpval,-1);
           int i, pos=-1;
           for(i=0;i<b.nvals;i++) {
        	   if(b.keys[i] > key) {
        		   pos=i;
        		   break;
        	   }
           }
           if(pos == -1) {
        	   pos = i;
           }
           
           for(i=0;i<b.nvals+1;i++) {
        	   if(i<pos) {
        		   tmpkey[i] = b.keys[i];
        		   tmpval[i] = b.vals[i];
        	   } else if(i==pos) {
        		   tmpkey[i] = key;
        		   tmpval[i] = val;
        	   } else {
        		   tmpkey[i] = b.keys[i-1];
        		   tmpval[i] = b.vals[i-1];
        	   }
           }
           tmpkey[fanout-1] = b.keys[fanout-1];
           buffer.clear();
           raf.seek(b.idx);
          for(i=0;i<fanout-1;i++) {
             buffer.putInt(tmpkey[i]);
             buffer.putInt(tmpval[i]);
          }
          buffer.putInt(tmpkey[i]);
          raf.write(buf);
          buffer.clear();
        }
    }
    
    private void insertInternalleaf(int left_idx, Block parent, Block right) throws IOException {
        if(parent == null) {
           Block root = new Block();
           root.idx = endindex;
           rootindex = root.idx;
           height++;
           endindex += blocksize;
           root.keys[0] = left_idx;
           root.vals[0] = right.keys[0];
           root.keys[1] = right.idx;
           buffer.clear();
           raf.seek(root.idx);
           int i;
           for(i=0; i<right.nvals; i++) {
               buffer.putInt(root.keys[i]);
               buffer.putInt(root.vals[i]);
           }
           buffer.putInt(root.keys[i]);
           raf.write(buf);
           buffer.clear();
           stack.clear();
        }
        else {
          if(parent.nvals >= fanout-1) { //full
             Block newnode = splitnonleaf(parent, right.idx, right.keys[0]);
             int parent_idx = parent.idx;
             newnode = new Block(newnode.idx);
             if(!stack.empty()) {
                 insertInternalnonleaf(parent_idx, readBlock(stack.pop()), newnode);
             }
             else {
                 insertInternalnonleaf(parent_idx, null, newnode);
             }
          } else { //not full
              int i,pos=-1;
              int[]  tmpkey = new int[fanout];
              int[]  tmpval = new int[fanout-1];
              Arrays.fill(tmpkey,-1);
              Arrays.fill(tmpval,-1);
              buffer.clear();
              
              for(i=0;i<parent.nvals;i++) {
           	   if(parent.vals[i] > right.keys[0]) {
           		   pos=i;
           		   break;
           	   }
              }
              if(pos == -1) {
           	   pos = i;
              }
              
              for(i=0;i<parent.nvals+1;i++) {
             	 if(i<pos) tmpval[i] = parent.vals[i];
             	 else if(i==pos) tmpval[i] = right.keys[0];
             	 else tmpval[i] = parent.vals[i-1];
              }
              for(i=0;i<parent.nvals+2;i++) {
             	 if(i<pos+1) tmpkey[i] = parent.keys[i];
             	 else if(i==pos+1) tmpkey[i] = right.idx;
             	 else tmpkey[i] = parent.keys[i-1];
              }

              raf.seek(parent.idx);
              for(i=0;i<fanout-1;i++) {
                 buffer.putInt(tmpkey[i]);
                 buffer.putInt(tmpval[i]);
              }
              buffer.putInt(tmpkey[i]);
              raf.write(buf);
              buffer.clear();
              stack.clear();
          }
        }
     }
    
    private void insertInternalnonleaf(int left_idx, Block parent, Block right) throws IOException {
       if(parent == null) {
              Block root = new Block();
              root.idx = endindex;
              rootindex = root.idx;
              height++;
              endindex += blocksize;
              root.keys[0] = left_idx;
              root.vals[0] = right.vals[0];
              root.keys[1] = right.idx;

              int i;
              for(i = 1; i < right.nvals; i++) {
                  right.vals[i-1] = right.vals[i];
              }
              right.vals[i-1] = -1;

              buffer.clear();
              raf.seek(root.idx);
              for(i=0; i<fanout-1; i++) {
                  buffer.putInt(root.keys[i]);
                  buffer.putInt(root.vals[i]);
              }
              buffer.putInt(root.keys[i]);
              raf.write(buf);
              buffer.clear();

              raf.seek(right.idx);
              for(i=0; i<fanout-1; i++) {
                  buffer.putInt(right.keys[i]);
                  buffer.putInt(right.vals[i]);
              }
              buffer.putInt(right.keys[i]);
              raf.write(buf);
              buffer.clear();
              stack.clear();
       }
       else {
         if(parent.nvals == fanout-1) { //full
            Block newnode = splitnonleaf(parent, right.idx, right.vals[0]);
            int parent_idx = parent.idx;
            newnode = new Block(newnode.idx);
            int i;
            for(i = 1; i < right.nvals; i++) {
                right.vals[i-1] = right.vals[i];
            }
            right.vals[i-1] = -1;
            right.nvals--;
            buffer.clear();
            raf.seek(right.idx);
            for(i=0;i<fanout-1;i++) {
               buffer.putInt(right.keys[i]);
               buffer.putInt(right.vals[i]);
            }
            buffer.putInt(right.keys[i]);
            raf.write(buf);
            buffer.clear();

            if(!stack.empty()) {
               insertInternalnonleaf(parent_idx, readBlock(stack.pop()), newnode);
            }
            else {
                insertInternalnonleaf(parent_idx, null, newnode);
            }
         } else { // not full
               int i,pos=-1;
               int[] tmpkey = new int[fanout];
               int[] tmpval = new int[fanout-1];
               Arrays.fill(tmpkey,-1);
               Arrays.fill(tmpval,-1);
               
               for(i=0;i<parent.nvals;i++) {
            	   if(parent.vals[i] > right.vals[0]) {
            		   pos=i;
            		   break;
            	   }
               }
               if(pos == -1) {
            	   pos = i;
               }
               
               for(i=0;i<parent.nvals+1;i++) {
              	 if(i<pos) tmpval[i] = parent.vals[i];
              	 else if(i==pos) tmpval[i] = right.vals[0];
              	 else tmpval[i] = parent.vals[i-1];
               }
               for(i=0;i<parent.nvals+2;i++) {
              	 if(i<pos+1) tmpkey[i] = parent.keys[i];
              	 else if(i==pos+1) tmpkey[i] = right.idx;
              	 else tmpkey[i] = parent.keys[i-1];
               }

               for(i = 1; i < right.nvals; i++) {
                  right.vals[i-1] = right.vals[i];
               }
               right.vals[i-1] = -1;
               right.nvals--;

               buffer.clear();
               raf.seek(right.idx);
               for(i=0;i<fanout-1;i++) {
                  buffer.putInt(right.keys[i]);
                  buffer.putInt(right.vals[i]);
               }
               buffer.putInt(right.keys[i]);
               raf.write(buf);
               buffer.clear();

               raf.seek(parent.idx);
               for(i = 0; i <fanout-1; i++) {
                   buffer.putInt(tmpkey[i]);
                   buffer.putInt(tmpval[i]);
               }
               buffer.putInt(tmpkey[i]);
               raf.write(buf);
               buffer.clear();
               stack.clear();
         }
       }
    }
    
    private Block splitleaf(Block b, int key, int val) throws IOException {
        Block child = new Block();
        child.idx = endindex;
        endindex += blocksize;
        int[] tmpkey = new int[fanout+1];
        int[] tmpval = new int[fanout];
        int i,pos=-1;

        Block origin = new Block();
        
        for(i=0;i<fanout-1;i++) {
     	   if(b.keys[i] > key) {
     		   pos=i;
     		   break;
     	   }
        }
        if(pos == -1) {
     	   pos = i;
        }
        
        for(i=0;i<fanout;i++) {
        	if(i<pos) {
        		tmpval[i] = b.vals[i];
        		tmpkey[i] = b.keys[i];
        	} else if(i==pos) {
        		tmpval[i] = val;
        		tmpkey[i] = key;
        	} else {
        		tmpval[i] = b.vals[i-1];
        		tmpkey[i] = b.keys[i-1];
        	}
        }
        int j=0;
    	for(i=0;i<tmpval.length;i++) {
        	if(i<tmpval.length/2) {
        		origin.vals[i] = tmpval[i];
        		origin.keys[i] = tmpkey[i];
        	} else {
        		child.vals[j] = tmpval[i];
        		child.keys[j] = tmpkey[i];
        		j++;
        		child.nvals++;
        	}
        }

        child.keys[fanout-1] = b.keys[fanout-1];
        origin.keys[fanout-1] = child.idx;

        raf.seek(b.idx);
        buffer.clear();
        for(i=0; i<fanout-1; i++) {
           buffer.putInt(origin.keys[i]);
           buffer.putInt(origin.vals[i]);
        }
        buffer.putInt(origin.keys[i]);
        raf.write(buf);
        buffer.clear();

        raf.seek(child.idx);
        for(i=0; i<fanout-1; i++) {
           buffer.putInt(child.keys[i]);
           buffer.putInt(child.vals[i]);
        }
        buffer.putInt(child.keys[i]);
        raf.write(buf);
        buffer.clear();

        return child;
    }
    
    private Block splitnonleaf(Block b, int key, int val) throws IOException {
        Block child = new Block();
        child.idx = endindex;
        endindex += blocksize;
        int[] tmpkey = new int[fanout+1];
        int[] tmpval = new int[fanout];
        int i,pos=-1;

        Block origin = new Block();
        
        for(i=0;i<fanout-1;i++) {
     	   if(b.vals[i] > val) {
     		   pos=i;
     		   break;
     	   }
        }
        if(pos == -1) {
     	   pos = i;
        }
        
        for(i=0;i<fanout;i++) {
        	if(i<pos) tmpval[i] = b.vals[i];
        	else if(i==pos) tmpval[i] = val;
        	else tmpval[i] = b.vals[i-1];
        }
        for(i=0;i<fanout+1;i++) {
        	if(i<pos+1) tmpkey[i] = b.keys[i];
        	else if(i==pos+1) tmpkey[i] = key;
        	else tmpkey[i] = b.keys[i-1];
        }

        System.arraycopy(tmpval, 0, origin.vals, 0, fanout/2);
        System.arraycopy(tmpval, fanout/2, child.vals, 0, fanout-(fanout/2));
    	System.arraycopy(tmpkey, 0, origin.keys, 0, fanout/2 + 1);
        System.arraycopy(tmpkey, fanout/2 + 1, child.keys, 0, fanout - (fanout/2));
        
        child.nvals = fanout-(fanout/2);

        raf.seek(b.idx);
        buffer.clear();
        for(i=0; i<fanout-1; i++) {
           buffer.putInt(origin.keys[i]);
           buffer.putInt(origin.vals[i]);
        }
        buffer.putInt(origin.keys[i]);
        raf.write(buf);
        buffer.clear();

        raf.seek(child.idx);
        for(i=0; i<fanout-1; i++) {
           buffer.putInt(child.keys[i]);
           buffer.putInt(child.vals[i]);
        }
        buffer.putInt(child.keys[i]);
        raf.write(buf);
        buffer.clear();

        return child;
    }

    public Block searchNode(int key) throws IOException {
        Block b = readBlock(rootindex);
         return _searchNode(b, key);
     }
     
    private Block _searchNode(Block b, int key) throws IOException {
         if (b.type == 1) { // non-leaf
            stack.push(b.idx);
            int i,pos=-1;
            for(i=0;i<b.nvals;i++) {
         	   if(b.vals[i] >= key) {
         		   pos=i;
         		   break;
         	   }
            }
            if(pos == -1) {
         	   pos = i;
            }
            
            try{
                if(b.vals[pos] == key) pos++;
            } catch (ArrayIndexOutOfBoundsException e){
            }

            Block child = readBlock(b.keys[pos]);
            return _searchNode(child,key);
         }
         else { // leaf
             return b;
         }
     }

    private Block readBlock(int rootindex) throws IOException {
        return new Block(rootindex);
    }
    public int search(int key) throws IOException {
        Block rb = readBlock(rootindex);
        return _search(rb, key);
    }
    private int _search(Block b, int key) throws IOException {
      if (b.type == 1) { // non-leaf
    	  stack.push(b.idx);
        int i,pos=-1;
        for(i=0;i<b.nvals;i++) {
     	   if(b.vals[i] >= key) {
     		   pos=i;
     		   break;
     	   }
        }
        if(pos == -1) {
     	   pos = i;
        }
        
        try{
            if(b.vals[pos] == key) pos++;
        } catch (ArrayIndexOutOfBoundsException e){
        }

        Block child = readBlock(b.keys[pos]);
        return _search(child,key);
      }
      else { // leaf
          stack.clear();
          int val = -1;
          for(int i=0;i<fanout-1;i++) {
        	  if(b.keys[i] == key) val = b.vals[i];
          }
          return val;
      }
   }

    /**
     * B+ tree dj cute
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        DataOutputStream dos = null;
         if (Files.exists(Paths.get(metafile)))
         {
             dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metafile)));
         }
         else {
            File f = new File(metafile);
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
         }
         dos.writeInt(rootindex);
         dos.writeInt(endindex);
         dos.writeInt(height);
         dos.close();
         raf.close();   
     }

    public class Block {
        public int idx;
        public int type;
        public int[] keys;
        public int[] vals;
        public int nvals;

        public Block(int idx) throws IOException {
             this.idx = idx;
             this.keys = new int[fanout];
             this.vals = new int[fanout-1];
             Arrays.fill(this.keys, -1);
             Arrays.fill(this.vals, -1);
             this.nvals = 0;
             buffer.clear();
             raf.seek(idx);
             raf.read(buf);
             buffer = ByteBuffer.wrap(buf);
             int i;
             for(i=0; i<fanout-1; i++) {
                 this.keys[i] = buffer.getInt();
                 this.vals[i] = buffer.getInt();
                 if(this.vals[i] != -1) nvals++;
             }
             this.keys[i] = buffer.getInt();
             if(stack.size() == height) {
                 this.type = 0; //leaf
                 this.keys[fanout-1] = buffer.getInt(blocksize-4); //확인 필요
             }
             else {
                 this.type = 1; //non-leaf
             }
        }
        public Block() {
           this.keys = new int[fanout];
           this.vals = new int[fanout-1];
           Arrays.fill(this.keys, -1);
           Arrays.fill(this.vals, -1);
        }
    }
}
