package edu.hanyang.submit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.lang3.tuple.MutableTriple;
import io.github.hyerica_bdml.indexer.ExternalSort;

public class HanyangSEExternalSort implements ExternalSort {
    /**
     * External sorting     
     * @param infile    Input file
     * @param outfile   Output file
     * @param tmpdir    Temporary directory to be used for writing intermediate runs on 
     * @param blocksize Available blocksize in the main memory of the current system
     * @param nblocks   Available block numbers in the main memory of the current system
     * @throws IOException  Exception while performing external sort
     */
  //정렬된 priority Queue를 받아서 file생성하고 쓰기 
    private void make_file(PriorityQueue<MutableTriple<Integer, Integer, Integer>> q, int fileNum, String tmpDir, int blksize) throws IOException {
      String fileName = tmpDir + fileNum + ".data"; //filePath
      File file = new File(fileName);
      DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName), blksize));
      MutableTriple<Integer, Integer, Integer> tmp;
      while((tmp = q.poll()) != null) {
         dos.writeInt(tmp.getLeft()); dos.writeInt(tmp.getMiddle()); dos.writeInt(tmp.getRight());
      }
      dos.close();
    }
    
    public void initial_run(File file, String tmpDir, int blksize) throws IOException {
       DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(file), blksize));
       int tupleCnt = 0, fileNum = 0;
       deleteFilesRecursively(new File("tmp/"));
       Files.createDirectory(Paths.get("tmp/"));
       PriorityQueue<MutableTriple<Integer, Integer, Integer>> dataArr = new PriorityQueue<>();//nElement = 3
       try {
    	   while(true) {
               while(tupleCnt < 3000) {
                   dataArr.add(new MutableTriple<Integer, Integer, Integer>(dos.readInt(), dos.readInt(), dos.readInt()));
                   tupleCnt++;             
               }
               tupleCnt = 0;
                   //함수에 Arraylist넣어서 정렬하고 정렬된 ArrayList 리턴
                   //ArrayList와 fileNum을받아서 이름이 fileNum인파일을 만들어주는 함수 생성
               make_file(dataArr, fileNum, tmpDir, blksize);
               fileNum++;
               dataArr.clear();
           }
       }catch(EOFException e) {
    	   make_file(dataArr, fileNum, tmpDir, blksize);
    	   dos.close();
       }
    }
    
    @Override
    public  void sort(String infile, String outfile, String tmpdir, int blocksize, int nblocks) throws IOException {
       //1) initial phase
        File dataFile = new File(infile);
        initial_run(dataFile, tmpdir, blocksize);
        
        //2) n-way merge
        _externalMergeSort(tmpdir, outfile, 0, blocksize, nblocks);
    }
 
    private void _externalMergeSort(String tmpDir, String outputFile, int step, int blksize, int nblk) throws IOException {
       String prevstep = "";
       if (step != 0) {
          prevstep = "tmp"+String.valueOf(step-1) + File.separator;
       }
       File[] fileArr = (new File(tmpDir + prevstep)).listFiles();
       //위에끝
       String name = String.valueOf(tmpDir + "tmp" + String.valueOf(step) + "/");
       Files.createDirectories(Paths.get(name));
       int num = 0;
       List<DataInputStream> file = new ArrayList<> (nblk-1);
       if (fileArr.length <= nblk-1) {
    	   DataInputStream dos = null;
          for (File f : fileArr) {
             dos = new DataInputStream(new BufferedInputStream(new FileInputStream(f.getAbsolutePath()), blksize));
             file.add(dos);
          }
          n_way_merge(file, tmpDir + "tmp" + String.valueOf(step) + File.separator, num, nblk);
          dos.close();
          File oldfile = new File(tmpDir + "tmp" + String.valueOf(step) + File.separator + "0.data"); 
          if (new File(outputFile).exists()) {
             new File(outputFile).delete();
          }
          File newfile = new File(outputFile);
          oldfile.renameTo(newfile);

       }
       else {
          int cnt = 0;
          DataInputStream dos = null;
          for (File f : fileArr) {
    		  dos = new DataInputStream(new BufferedInputStream(new FileInputStream(f.getAbsolutePath()), blksize));
              file.add(dos);
              cnt++;
              if(cnt == nblk-1) {
        		  n_way_merge(file, tmpDir + "tmp" + String.valueOf(step) + File.separator, num, nblk);
            	  file.clear();
            	  dos.close();
                  num ++;
        		  cnt = 0;
              }
          }
          n_way_merge(file, tmpDir + "tmp" + String.valueOf(step) + File.separator, num, nblk);
          file.clear();
    	  dos.close();
          _externalMergeSort(tmpDir, outputFile, step+1, blksize, nblk);
       }
    }
    //files : data파일들
    public void n_way_merge(List<DataInputStream> files, String outputFile, int num, int nblk) throws IOException {
       PriorityQueue <DataManager> queue = new PriorityQueue<>(files.size(), new Comparator<DataManager>() {
          public int compare(DataManager o1, DataManager o2) {
             return o1.tuple.compareTo(o2.tuple);
          }
       });
       
       for(DataInputStream ds : files) {
          queue.add(new DataManager(ds));
       }
       
       
       //queue에 맨 처음 값만 비교해서 작은 것들 넣고 두번 째, 세번 째 값은 queue에서 비교해서 정렬해줌
       String tmpfileName = outputFile + num + ".data"; //filePath
       File file = new File(tmpfileName);
       MutableTriple <Integer, Integer, Integer> tmp = new MutableTriple <Integer, Integer, Integer>();
       DataOutputStream mas = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpfileName), nblk));
       while (queue.size() != 0) {
          DataManager dm = queue.poll();
              if(!dm.isEOF) {
                 dm.getTuple(tmp);
                 mas.writeInt(tmp.getLeft()); mas.writeInt(tmp.getMiddle()); mas.writeInt(tmp.getRight());
                 queue.add(dm);
              }
       }
       mas.close();
    }
    
    public boolean deleteFilesRecursively(File rootFile) {
        File[] allFiles = rootFile.listFiles();
        if (allFiles != null) {
            for (File file : allFiles) {
                deleteFilesRecursively(file);
            }
        }
//        System.out.println("Remove file: " + rootFile.getPath());
        return rootFile.delete();

    }
}

class DataManager {
      public boolean isEOF = false;
      private DataInputStream dis = null;
      public MutableTriple<Integer, Integer, Integer> tuple = new MutableTriple<Integer, Integer, Integer>(0, 0, 0);
      public DataManager(DataInputStream dis) throws IOException {
         this.dis = dis;
         tuple.setLeft(dis.readInt()); tuple.setMiddle(dis.readInt()); tuple.setRight(dis.readInt());
      }
      boolean readNext() throws IOException {
         try {
        	 if (isEOF) return false;
        	 tuple.setLeft(dis.readInt()); tuple.setMiddle(dis.readInt()); tuple.setRight(dis.readInt());
             return true; 
         } catch(EOFException e) {
        	 return false;
         }
      }
      public void getTuple(MutableTriple<Integer, Integer, Integer> ret) throws IOException {
         ret.setLeft(tuple.getLeft()); ret.setMiddle(tuple.getMiddle()); ret.setRight(tuple.getRight());
         isEOF = (! readNext());
      }
      //추가로 선언
}