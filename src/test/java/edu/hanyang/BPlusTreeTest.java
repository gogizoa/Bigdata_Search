//package edu.hanyang.test;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.junit.Ignore;
//import org.junit.Test;
//import static org.junit.Assert.assertEquals;
//
//import edu.hanyang.submit.HanyangSEBPlusTree;
//
//public class BPlusTreeTest {
//
//    @Test
//    public void bPlusTreeTest() throws IOException {
//        String metapath = "tmp/metadata.txt";
//        String savepath = "tmp/bplustree.data";
//        int blocksize = 20;
//        int nblocks = 10;
//
//        File treefile = new File(savepath);
//        if (treefile.exists()) {
//            if (! treefile.delete()) {
//                System.err.println("error: cannot remove files");
//                System.exit(1);
//            }
//        }
//
//        HanyangSEBPlusTree tree = new HanyangSEBPlusTree();
//        tree.open(metapath, savepath, blocksize, nblocks);
//        System.out.println("insert(5,10)");
//        tree.insert(5, 10);
//        System.out.println("insert(6,15)");
//        tree.insert(6, 15);
//        System.out.println("insert(4,20)");
//        tree.insert(4, 20);
//        tree.printBlock();
//        System.out.println("insert(7,1)");
//        tree.insert(7, 1);
//        System.out.println("insert(8,5)");
//        tree.insert(8, 5);
//        System.out.println("insert(17,7)");
//        tree.insert(17, 7);
//        System.out.println("insert(30,8)");
//        tree.insert(30, 8);
//        tree.printBlock();
//        System.out.println("insert(40,8)");
//        tree.insert(40, 8);
//        System.out.println("insert(58,1)");
//        tree.insert(58, 1);
//        System.out.println("insert(60,8)");
//        tree.insert(60, 8);
//        System.out.println("insert(96,32)");
//        tree.insert(96, 32);
//        System.out.println("insert(100,8)");
//        tree.insert(100, 8);
//        System.out.println("insert(110,98)");
//        tree.insert(110, 98);
//        System.out.println("insert(120,54)");
//        tree.insert(120, 54);
//        System.out.println("insert(157,54)");
//        tree.insert(157, 54);
//        System.out.println("insert(247,54)");
//        tree.insert(247, 54);
//        System.out.println("insert(337,254)");
//        tree.insert(357, 254);
//        System.out.println("insert(557,54)");
//        tree.insert(557, 54);
//
//
//        tree.close();
//
//        // check read and write and result of tree
//        tree = new HanyangSEBPlusTree();
//        tree.open(metapath, savepath, blocksize, nblocks);
//
////         Check search function
//        System.out.println("search(5,10)");
//        assertEquals(tree.search(5), 10);
//        System.out.println("search(6,15)");
//        assertEquals(tree.search(6), 15);
//        System.out.println("search(4,20)");
//        assertEquals(tree.search(4), 20);
//        System.out.println("search(7,1)");
//        assertEquals(tree.search(7), 1);
//        System.out.println("search(8,5)");
//        assertEquals(tree.search(8), 5);
//        System.out.println("search(17,7)");
//        assertEquals(tree.search(17), 7);
//        System.out.println("search(30,8)");
//        assertEquals(tree.search(30), 8);
//        System.out.println("search(40,8)");
//        assertEquals(tree.search(40), 8);
//        System.out.println("search(58,1)");
//        assertEquals(tree.search(58), 1);
//        System.out.println("search(60,8)");
//        assertEquals(tree.search(60), 8);
//        System.out.println("search(96,32)");
//        assertEquals(tree.search(96), 32);
//        System.out.println("search(100,8)");
//        assertEquals(tree.search(100), 8);
//        System.out.println("search(110,98)");
//        assertEquals(tree.search(110), 98);
//        System.out.println("search(120,54)");
//        assertEquals(tree.search(120), 54);
//        System.out.println("search(157,54)");
//        assertEquals(tree.search(157), 54);
//        System.out.println("search(247,54)");
//        assertEquals(tree.search(247), 54);
//        System.out.println("search(357,254)");
//        assertEquals(tree.search(357), 254);
//        System.out.println("search(557,54)");
//        assertEquals(tree.search(557), 54);
//
//        tree.close();
//    }
//}

package edu.hanyang;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import edu.hanyang.submit.HanyangSEBPlusTree;

public class BPlusTreeTest {

    @Test
    public void bPlusTreeTest() throws IOException {
        String metapath = "tmp/metadata.txt";
        String savepath = "tmp/bplustree.data";
        int blocksize = 20;
        int nblocks = 10;

        File treefile = new File(savepath);
        if (treefile.exists()) {
            if (! treefile.delete()) {
                System.err.println("error: cannot remove files");
                System.exit(1);
            }
        }

        HanyangSEBPlusTree tree = new HanyangSEBPlusTree();
        tree.open(metapath, savepath, blocksize, nblocks);
        System.out.println("insert(5,10)");
        tree.insert(5, 10);
        System.out.println("insert(6,15)");
        tree.insert(6, 15);
        System.out.println("insert(4,20)");
        tree.insert(4, 20);
        System.out.println("insert(7,1)");
        tree.insert(7, 1);
        System.out.println("insert(8,5)");
        tree.insert(8, 5);
        System.out.println("insert(17,7)");
        tree.insert(17, 7);
        System.out.println("insert(30,8)");
        tree.insert(30, 8);
        System.out.println("insert(40,8)");
        tree.insert(40, 8);
        System.out.println("insert(58,1)");
        tree.insert(58, 1);
        System.out.println("insert(60,8)");
        tree.insert(60, 8);
        System.out.println("insert(96,32)");
        tree.insert(96, 32);
        System.out.println("insert(100,8)");
        tree.insert(100, 8);
        System.out.println("insert(110,98)");
        tree.insert(110, 98);
        System.out.println("insert(120,54)");
        tree.insert(120, 54);
        System.out.println("insert(157,54)");
        tree.insert(157, 54);
        System.out.println("insert(247,54)");
        tree.insert(247, 54);
        System.out.println("insert(337,254)");
        tree.insert(357, 254);
        System.out.println("insert(557,54)");
        tree.insert(557, 54);
        System.out.println("insert(200,54)");
        tree.insert(200, 54);
        System.out.println("insert(600,54)");
        tree.insert(600, 54);
        System.out.println("insert(650,54)");
        tree.insert(650, 54);
        System.out.println("insert(700,54)");
        tree.insert(700, 54);
        System.out.println("insert(800,54)");
        tree.insert(800, 54);
        System.out.println("insert(900,54)");
        tree.insert(900, 54);
        System.out.println("insert(1000,54)");
        tree.insert(1000, 54);
        System.out.println("insert(122,54)");
        tree.insert(122, 54);
        System.out.println("insert(1050,54)");
        tree.insert(1050, 54);
        System.out.println("insert(1100,54)");
        tree.insert(1100, 54);


        tree.close();

        // check read and write and result of tree
        tree = new HanyangSEBPlusTree();
        tree.open(metapath, savepath, blocksize, nblocks);

//         Check search function
        System.out.println("search(5,10)");
        assertEquals(tree.search(5), 10);
        System.out.println("search(6,15)");
        assertEquals(tree.search(6), 15);
        System.out.println("search(4,20)");
        assertEquals(tree.search(4), 20);
        System.out.println("search(7,1)");
        assertEquals(tree.search(7), 1);
        System.out.println("search(8,5)");
        assertEquals(tree.search(8), 5);
        System.out.println("search(17,7)");
        assertEquals(tree.search(17), 7);
        System.out.println("search(30,8)");
        assertEquals(tree.search(30), 8);
        System.out.println("search(40,8)");
        assertEquals(tree.search(40), 8);
        System.out.println("search(58,1)");
        assertEquals(tree.search(58), 1);
        System.out.println("search(60,8)");
        assertEquals(tree.search(60), 8);
        System.out.println("search(96,32)");
        assertEquals(tree.search(96), 32);
        System.out.println("search(100,8)");
        assertEquals(tree.search(100), 8);
        System.out.println("search(110,98)");
        assertEquals(tree.search(110), 98);
        System.out.println("search(120,54)");
        assertEquals(tree.search(120), 54);
        System.out.println("search(157,54)");
        assertEquals(tree.search(157), 54);
        System.out.println("search(247,54)");
        assertEquals(tree.search(247), 54);
        System.out.println("search(357,254)");
        assertEquals(tree.search(357), 254);
        System.out.println("search(557,54)");
        assertEquals(tree.search(557), 54);
        System.out.println("insert(200,54)");
        assertEquals(tree.search(200), 54);
        System.out.println("insert(600,54)");
        assertEquals(tree.search(600), 54);
        System.out.println("insert(650,54)");
        assertEquals(tree.search(650), 54);
        System.out.println("insert(700,54)");
        assertEquals(tree.search(700), 54);
        System.out.println("insert(800,54)");
        assertEquals(tree.search(800), 54);
        System.out.println("insert(900,54)");
        assertEquals(tree.search(900), 54);
        System.out.println("insert(1000,54)");
        assertEquals(tree.search(1000), 54);
        System.out.println("insert(122,54)");
        assertEquals(tree.search(122), 54);
        System.out.println("insert(1050,54)");
        assertEquals(tree.search(1050), 54);
        System.out.println("insert(1100,54)");
        assertEquals(tree.search(1100), 54);

        tree.close();
    }
}