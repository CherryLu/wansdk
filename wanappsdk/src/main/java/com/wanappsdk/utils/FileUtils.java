/****************************
 * 文件名:FileUtils.java
 * <p>
 * 创建时间:2013-3-27
 * 所在包:
 * 作者:罗泽锋
 * 说明:文件模块通用工具类
 ****************************/

package com.wanappsdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Locale;

public class FileUtils {

    // 获取应用程序基础路径，TODO：需要考虑有卡和无卡的情况
    public static String getAppBasePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "com.wansdk.data" + "/";
        return path;
    }
    /**
     * 保存图片
     */
    public static void saveBitmap(Bitmap bmp, String fileName) {
        try {
            if (TextUtils.isEmpty(fileName)) {
                return;
            }
            // make sure this file exist
            makesureFileExist(fileName);
            FileOutputStream out = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // 获取指定utf-8编码文件内容，返回字节数组
    public static byte[] getFileDataByte(String path) {
        byte[] res = null;
        try {
            FileInputStream fin = new FileInputStream(path);
            int length = fin.available();
            if (length > 0) {
                byte[] buffer = new byte[length];
                fin.read(buffer);
                res = buffer;
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // 复制打包进入的文件
    public static void copyAssetsFile(Context context, String srcName,
                                      String destName) {
        String fname = getAppBasePath() + destName;
        File file = new File(fname);

        // 如果目标文件已存在，则不复制
        if (file.exists()) {
            return;
        }

        try {
            makesureFileExist(fname);

            InputStream inReadFile = context.getAssets().open(srcName);
            int size = inReadFile.available();
            if (size > 0) {
                byte[] buffer = new byte[size];
                inReadFile.read(buffer);
                FileOutputStream fos = new FileOutputStream(file, false);
                fos.write(buffer);
                fos.close();
            }
            inReadFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 转换url地址为文件名，去掉http头和ip字段
    public static String convertFilenameFromUrl(String open_url, String param) {
        return convertFilenameFromUrl(open_url + "_" + param);
    }

    // 转换url地址为文件名，去掉http头和ip字段
    public static String convertFilenameFromUrl(String open_url) {
        String name = open_url;
        name = name.replace("http://", "");
        name = name.replace("https://", "");
        int pos = name.indexOf("/");
        if (pos > 0) {
            name = name.substring(pos);
        }
        name = name.replace("/", "_");
        name = name.replace("?", "_");
        name = name.replace("&", "_");
        name = name.replace("=", "_");
        return name;
    }

    // 数组保存为文件
    public static void saveAsFile(byte[] data, String fileName) {
        saveAsFile(data, fileName, false);
    }

    // 数组保存为文件
    public static void saveAsFile(byte[] data, String fileName, boolean append) {
        try {
            InputStream is = new ByteArrayInputStream(data);
            FileUtils.saveAsFile(is, fileName, false);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 输入流保存为文件
    public static void saveAsFile(InputStream inputStream, String fileName) {
        saveAsFile(inputStream, fileName, false);
    }

    // 输入流保存为文件
    public static void saveAsFile(InputStream inputStream, String fileName,
                                  boolean append) {
        try {
            if (TextUtils.isEmpty(fileName)) {
                return;
            }
            // make sure this file exist
            makesureFileExist(fileName);

            OutputStream os = new FileOutputStream(fileName, append);
            byte[] buf = new byte[255];
            int len = 0;
            while ((len = inputStream.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            inputStream.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 确定指定文件是否存在，如果不存在，则创建空文件
    public static void makesureFileExist(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        // file path
        int index = fileName.lastIndexOf("/");
        File file = null;
        if (index != -1) {
            String filePath = fileName.substring(0, index);
            file = new File(filePath);
            if (!file.exists()) {
                boolean ret = file.mkdirs();
            }
        }
        file = new File(fileName);
        if (!file.exists()) {// 确保文件存在
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     */

    public static void deleteFile(String fileName) {
        File file = new File(getAppBasePath() + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

//	public static class FileData implements LocalFileUtils, Serializable {
//		/**
//		 *
//		 */
//		public boolean isBackPath = false; // 是否是返回上一级
//		public boolean isPath = false; // 是否是目录
//		public boolean mIsDelete = false;
//		public String filePath = ""; // 文件全路径
//		public long length = 0; // 文件长度
//		public String fileName = ""; // 文件名
//		public int fileCount = 0;
//		public int floderCount = 0;
//		private String mIndex;
//		public boolean isBackPlay = false;
//		private boolean isHasDownloadingFile = false;
//
//		public boolean isHasDownloadingFile() {
//			return isHasDownloadingFile;
//		}
//
//		public void setHasDownloadingFile(boolean isHasDownloadingFile) {
//			this.isHasDownloadingFile = isHasDownloadingFile;
//		}
//
//		@Override
//		public String getPath() {
//			// TODO Auto-generated method stub
//			return filePath;
//		}
//
//		@Override
//		public String getName() {
//			// TODO Auto-generated method stub
//			return fileName;
//		}
//
//		public void setIndex(String mIndex) {
//			this.mIndex = mIndex;
//		}
//
//		@Override
//		public void setIsDelete(boolean isDel) {
//			mIsDelete = isDel;
//		}
//
//		@Override
//		public boolean getIsDelete() {
//			// TODO Auto-generated method stub
//			return mIsDelete;
//		}
//
//		@Override
//		public void delMySelf() {
//			if (isHasDownloadingFile) {
//				ArrayList<DownloadData> list = DownloadManager.getInstance()
//						.getFileList();
//				ArrayList<DownloadData> delList = new ArrayList<DownloadData>();
//				for (int i = 0; i < list.size(); i++) {
//					DownloadData dld = list.get(i);
//					if (dld.type == 1) {
//						String saveFloder = dld.saveFloder;
//						saveFloder = saveFloder.substring(0,
//								saveFloder.length() - 1);
//						if (getPath().subSequence(
//								getPath().length() - saveFloder.length(),
//								getPath().length()).equals(saveFloder)) {
//							// dld.delMyself();
//							delList.add(dld);
//						}
//					}
//				}
//				for (int i = 0; i < delList.size(); i++) {
//					delList.get(i).delMyself();
//				}
//			}
//			File f = new File(getPath());
//			if (f.exists()) {
//				if (f.isDirectory()) {
//					File[] childFiles = f.listFiles();
//					if (childFiles == null || childFiles.length == 0) {
//						f.delete();
//						return;
//					}
//					for (int i = 0; i < childFiles.length; i++) {
//						childFiles[i].delete();
//					}
//				} else {
//					f.delete();
//					File file = new File(getPath() + ".info");
//					if (file.exists()) {
//						file.delete();
//					}
//				}
//			}
//		}
//
//		// @Override
//		// public int describeContents() {
//		// // TODO Auto-generated method stub
//		// return 0;
//		// }
//		//
//		// @Override
//		// public void writeToParcel(Parcel p, int arg1) {
//		// p.writeInt(isBackPath ? 0 : 1);
//		// p.writeInt(isPath ? 0 : 1);
//		// p.writeInt(mIsDelete ? 0 : 1);
//		// p.writeString(filePath);
//		// p.writeLong(length);
//		// p.writeString(fileName);
//		// p.writeInt(fileCount);
//		// p.writeInt(floderCount);
//		// p.writeString(mIndex);
//		// }
//		//
//		// public static final Parcelable.Creator<FileData> CREATOR = new
//		// Parcelable.Creator<FileData>() {
//		// @Override
//		// public FileData createFromParcel(Parcel p) {
//		// FileData fd = new FileData();
//		// fd.isBackPath= (p.readInt() == 0);
//		// fd.isPath= (p.readInt() == 0);
//		// fd.mIsDelete= (p.readInt() == 0);
//		// fd.filePath = p.readString();
//		// fd.length = p.readLong();
//		// fd.fileName = p.readString();
//		// fd.fileCount = p.readInt();
//		// fd.floderCount = p.readInt();
//		// fd.mIndex = p.readString();
//		// return fd;
//		// }
//		//
//		// @Override
//		// public FileData[] newArray(int arg0) {
//		// // TODO Auto-generated method stub
//		// return null;
//		// }
//		// };
//
//	}

    public static int getFileCount(File file) {
        int count = 0;
        File[] flist = file.listFiles();

        if (flist == null) {
            return 0;
        }

        int len = flist.length;

        for (int i = 0; i < len; i++) {
            boolean isPath = !flist[i].isFile();
            if (!isPath && !supportFile(flist[i].getName()))
                continue;
            count++;
        }
        return count;
    }

    public static int getProgramFileCount(File file) {
        int count = 0;
        File[] flist = file.listFiles();

        if (flist == null) {
            return 0;
        }

        int len = flist.length;

        for (int i = 0; i < len; i++) {
            boolean isPath = !flist[i].isFile();
            if (!isPath && !supportProgramFile(flist[i].getName()))
                continue;
            count++;
        }
        return count;
    }

    public static int getFloderCount(File file) {
        int count = 0;
        File[] flist = file.listFiles();

        if (flist == null) {
            return 0;
        }

        int len = flist.length;

        for (int i = 0; i < len; i++) {
            if (!flist[i].isFile())
                count++;
        }
        return count;
    }

//

    private static boolean isNullDir(File file) {
        if (file != null) {
            File files[] = file.listFiles();
            if (files != null && files.length > 0) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static void sortByName(File[] files, String path, final boolean order) {
      /*  if (files == null)
            return;
        ArrayList<File> list = new ArrayList<File>();
        for (File f : files) {
            list.add(f);
        }
        Collections.sort(list, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                File file1 = (File) obj1;
                File file2 = (File) obj2;
                String index1 = "";
                String index2 = "";
                Object ob1 = ObjectUtils.loadObjectData(file1.getAbsolutePath()
                        + ".info");
                Object ob2 = ObjectUtils.loadObjectData(file2.getAbsolutePath()
                        + ".info");

                if (ob1 != null && ob1 instanceof ChaptersData) {
                    index1 = ((ChaptersData) ob1).sequence_time;
                }
                if (ob1 != null && ob1 instanceof String) {
                    index1 = (String) ob1;
                }

                if (ob2 != null && ob2 instanceof ChaptersData) {
                    index2 = ((ChaptersData) ob2).sequence_time;
                }

                if (ob2 != null && ob2 instanceof String) {
                    index2 = (String) ob2;
                }

                if (!TextUtils.isEmpty(index1) && !TextUtils.isEmpty(index2)) {
                    if (order) {
                        return 0 - Collator.getInstance(Locale.CHINESE)
                                .compare(index1, index2);
                    } else {
                        return Collator.getInstance(Locale.CHINESE).compare(
                                index1, index2);
                    }

                } else if (!TextUtils.isEmpty(index1)
                        && TextUtils.isEmpty(index2)) {
                    if (order) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (TextUtils.isEmpty(index1)
                        && !TextUtils.isEmpty(index2)) {
                    if (order) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    if (order) {
                        return Collator.getInstance(Locale.CHINESE).compare(
                                file1.getName(), file2.getName());
                    } else {
                        return 0 - Collator.getInstance(Locale.CHINESE)
                                .compare(file1.getName(), file2.getName());
                    }
                }
            }
        });
        for (int i = 0; i < list.size(); i++) {
            files[i] = list.get(i);
        }*/
    }

    public static boolean supportProgramFile(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return false;

        if (fileName.length() < 4)
            return false;
        int begin = fileName.lastIndexOf(".");
        if (begin > 0) {
            String subString = fileName.substring(begin + 1, fileName.length());
            if (subString.toLowerCase().indexOf("aac") >= 0) {
                return true;
            }
        }
        // if (fileName.substring(fileName.length() - 4, fileName.length())
        // .toLowerCase().equals(".aac"))
        // return true;
        return false;
    }

    public static boolean supportFile(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return false;

        if (fileName.length() < 4)
            return false;

        if (fileName.substring(fileName.length() - 4, fileName.length())
                .toLowerCase().equals(".mp3"))
            return true;
        int begin = fileName.lastIndexOf(".");
        if (begin > 0) {
            String subString = fileName.substring(begin + 1, fileName.length());
            if (subString.toLowerCase().indexOf("aac") >= 0) {
                return true;
            }
        }
        return false;
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fileName = f.getName();
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4") || end.equals("avi")) {
            type = "video/*";
        } else if (end.equals("htm") || end.equals("html")) {
            type = "text/html";
        } else if (end.equals("jpg") || end.equals("pl/droidsonroids/gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp") || end.equals("ico")) {
            type = "image/*";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "text/plain";
        }
        return type;
    }





    // 递归删除文件及文件夹
    public static void deleteFileOrPath(String path) {
        File file = new File(path);
        deleteFileInfo(path);
        deleteFileOrPath(file);
    }
    //删除某目录下文件
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
    /**
     * 删除文件后缀是info的文件
     *
     * @param path
     */
    private static void deleteFileInfo(String path) {
        // TODO Auto-generated method stub
        if (path.endsWith(".tmp")) {
            path = path.substring(0, path.length() - 4);
        }
        File file = new File(path + ".info");
        if (file.exists()) {
            file.delete();
        }
    }

    // 递归删除文件及文件夹
    public static void deleteFileOrPath(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFileOrPath(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * @param src 源文件路径
     * @param out 新文件路径
     */
    public static void coypFile(@NonNull String src, @NonNull String out) {
        File file = new File(src);
        if (!file.exists() || file.isDirectory())
            return;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(out);
            inputStream = new FileInputStream(src);
            byte[] bytes = new byte[1024];
            int num = 0;
            while ((num = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, num);
                outputStream.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class Sort implements Comparator<File> {
        @Override
        public int compare(File arg0, File arg1) {
            // TODO Auto-generated method stub
            String file1 = arg0.getName();
            String file2 = arg1.getName();
            Collator cmp = Collator.getInstance(Locale.CHINA);
            return cmp.compare(file1, file2);
        }
    }



    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
    public static double getFileOrFilesSize(String filePath, int sizeType, String filterName) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file,filterName);
            } else {
                blockSize = getFileSize(file,filterName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file, String filterName) throws Exception {
        long size = 0;
        if (file.exists()) {
//            NewLogUtils.d("getfile","","file.getName()="+file.getName());
            if(!TextUtils.isEmpty(filterName)&&!file.getName().endsWith(filterName)){
//                NewLogUtils.d("getfile","","file.getName()11111111="+file.getName());
                return 0;
            }
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f, String filterName) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i],filterName);
            } else {
                size = size + getFileSize(flist[i],filterName);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }


    // 从文件中读取数据。
    public static Object loadObjectData(String path) {
        Object ret = null;
        if (path == null)
            return ret;

        File file = new File(path);
        if (!file.exists())
            return ret;

        try {
            FileInputStream fis = new FileInputStream(file);
            ret = loadObjectData(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // 从文件输入流中读取数据
    public static Object loadObjectData(InputStream inputStream) {
        Object ret = null;
        try {
            ObjectInputStream istream = null;
            istream = new ObjectInputStream(inputStream);
            ret = istream.readObject();
            istream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
