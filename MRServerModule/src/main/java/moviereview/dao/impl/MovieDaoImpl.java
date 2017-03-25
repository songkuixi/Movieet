package moviereview.dao.impl;

import moviereview.util.ShellUtil;
import moviereview.dao.MovieDao;
import moviereview.model.Movie;
import moviereview.model.Review;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kray on 2017/3/7.
 */

@Repository
public class MovieDaoImpl implements MovieDao {

    //file
    private File movieIndexFile;
    private File userIndexFile;
    private File movieIndexWithNameFile;
    private File tempResultFile;

    /**
     * writer
     */
    //BufferedWriter
    private BufferedWriter resultBufferedWriter;
    private BufferedWriter movieIndexBufferedWriter;
    private BufferedWriter userIndexBufferedWriter;
    private BufferedWriter tempResultBufferedWriter;

    /**
     * reader
     */
    //BufferedReader
    private BufferedReader sourceFileBufferedReader;

    /**
     * logger
     */
    private Logger logger;
    private FileHandler fileHandler;

    /**
     * 分割源文件并索引
     */
    public MovieDaoImpl() {
        //初始化file
        File resultFile = new File(DataConst.FILE_LOCATION + "/result0.txt");
        movieIndexFile = new File(DataConst.FILE_LOCATION + "/movieIndex.txt");
        userIndexFile = new File(DataConst.FILE_LOCATION + "/userIndex.txt");
        movieIndexWithNameFile = new File(DataConst.FILE_LOCATION + "/movieIndexWithName.txt");
        tempResultFile = new File(DataConst.PYTHON_FILE_LOCATION + "/tempResult.txt");
        //初始化一级I/O
        try {
            FileWriter resultWriter = new FileWriter(resultFile, true);
            FileWriter movieIndexWriter = new FileWriter(movieIndexFile, true);
            FileWriter userIndexWriter = new FileWriter(userIndexFile, true);
            FileWriter tempResultWriter = new FileWriter(tempResultFile, false);

            movieIndexBufferedWriter = new BufferedWriter(movieIndexWriter);
            resultBufferedWriter = new BufferedWriter(resultWriter);
            userIndexBufferedWriter = new BufferedWriter(userIndexWriter);
            tempResultBufferedWriter = new BufferedWriter(tempResultWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化缓存I/O

        //初始化logger
        initLogger();
    }


    /**
     * flush buffer
     */
    private void flushFiles() {
        try {
            movieIndexBufferedWriter.flush();
            resultBufferedWriter.flush();
            userIndexBufferedWriter.flush();
            tempResultBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * change a file to record new data
     *
     * @param i data NO.
     * @throws IOException handle by up level
     */
    private BufferedWriter changeFileToWrite(BufferedWriter resultBufferedWriter, int i) throws IOException {
        if (resultBufferedWriter != null) {
            resultBufferedWriter.close();
        }

        File fileToWrite = new File(DataConst.FILE_LOCATION + "/result" + DataConst.getFileIndex(i) + ".txt");
        FileWriter resultWriter = new FileWriter(fileToWrite, true);
        return new BufferedWriter(resultWriter);
    }

    /**
     * close file stream
     */
    private void closeFiles() {
        try {
            movieIndexBufferedWriter.close();
            sourceFileBufferedReader.close();
            resultBufferedWriter.close();
            userIndexBufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get BufferedReader connecting to certain file
     *
     * @param fileToRead certain file
     * @return BufferedReader connecting to certain file
     */
    private BufferedReader getBufferedReader(File fileToRead) {
        FileReader reader = null;
        try {
            reader = new FileReader(fileToRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert reader != null : "can't connect to file " + fileToRead.getName();

        return new BufferedReader(reader);
    }

    /**
     * get BufferedWriter connecting to certain file
     *
     * @param fileToWrite certain file
     * @return BufferedWriter connecting to certain file
     */
    private BufferedWriter getBufferedWriter(File fileToWrite, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileToWrite, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null : "can't connect to file " + movieIndexFile.getName();

        return new BufferedWriter(writer);
    }

    /**
     * handle the page fault
     */
    private BufferedReader changeFileToRead(BufferedReader beginBufferedReader, int dataNumber) throws IOException {
        if (beginBufferedReader != null) {
            beginBufferedReader.close();
        }
        File fileToRead = new File(DataConst.FILE_LOCATION + "/result" + DataConst.getFileIndex(dataNumber) + ".txt");
        FileReader beginFileReader = new FileReader(fileToRead);
        return new BufferedReader(beginFileReader);
    }

    private Review parseDataToReviewPO(BufferedReader reader) {
        String[] props = new String[8];
        try {
            for (int i = 0; i < 8; i++) {
                String[] temp = reader.readLine().split(": ");
                if (temp.length == 1) {
                    props[i] = "-1";
                } else {
                    props[i] = temp[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Review(props[0]
                , props[1]
                , props[2]
                , props[3]
                , Integer.parseInt(props[4].split("\\.")[0])
                , Long.parseLong(props[5])
                , props[6]
                , props[7]
        );
    }

    private void initLogger() {
        logger = Logger.getLogger("DataLogger");

        //设置handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        fileHandler = null;
        try {
            fileHandler = new FileHandler("../log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        fileHandler.setLevel(Level.CONFIG);
        logger.addHandler(fileHandler);

        //设置formatter
        fileHandler.setFormatter(new DataLogFormatter());
    }

    /**
     * close all files
     * warning! This method should only be invoked when exit the system
     */
    public void close() {
        closeFiles();
        fileHandler.close();
    }

    /**
     * 通过电影ID寻找指定的电影
     *
     * @param productId 电影ID
     * @return 指定的电影
     */
    public Movie findMovieByMovieId(String productId) {
        BufferedReader indexBufferedReader = getBufferedReader(movieIndexWithNameFile);
        //在索引中寻找
        String temp = null;
        //查询时必要的组件和缓存
        BufferedReader beginBufferedReader = null;
        //保存结果
        Movie movie = new Movie();
        try {
            while (true) {
                temp = indexBufferedReader.readLine();

                if (temp == null) {
                    break;
                }

                //找 ID
                String[] splitResult = temp.split(",");
                //如果 ID 匹配,找到,设定名字、ID
                if (splitResult[0].equals(productId)) {
                    movie.setId(splitResult[0]);

                    String movieName = "";
                    for (int i = 1; i < splitResult.length; i++) {
                        movieName += splitResult[i];
                    }
                    movie.setName(movieName);

                    return movie;
                }
            }
            //找不到电影
            return new Movie("-1", "Not Found");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (beginBufferedReader != null) {
                    beginBufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Movie("-1", "Not Found");
    }

    /**
     * 通过电影 ID 寻找该电影在 IMDB 上的 JSON 串
     *
     * @param productId 电影 ID
     * @return JSON 形式的 String
     */
    public Map<String, Object> findIMDBJsonStringByMovieId(String productId) {
        String stringResult = ShellUtil.getResultOfShellFromCommand("python3 " + DataConst.PYTHON_FILE_LOCATION + "/MovieIMDBGetter.py " + productId.toString());
        try {
            JSONObject jsonObject = new JSONObject(stringResult);
            return jsonObject.toMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}
