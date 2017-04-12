package util;

/**
 * Created by vivian on 2017/3/27.
 * 电影分类
 * 以下分类来源于IMDB中已经出现的数据
 */
public enum MovieGenre {
    NA,  //没有分类
    Action ,
    Adventure ,
    Horror ,
    Romance ,
    War ,
    History ,
    Documentary ,
    SciFi ,
    Sport ,
    Drama ,
    Thriller ,
    Music ,
    Crime ,
    Fantasy ,
    Biography ,
    Animation ,
    Family ,
    Mystery ,
    Comedy ,
    Musical ,
    Short;

    public String getGenreName() {
        return toString().charAt(0) + toString().toLowerCase().substring(1);
    }
}
