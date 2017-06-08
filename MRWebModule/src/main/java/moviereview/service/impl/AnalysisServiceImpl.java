package moviereview.service.impl;

import moviereview.bean.CountryCountBean;
import moviereview.bean.CountryScoreInYearBean;
import moviereview.repository.CountryRepository;
import moviereview.repository.MovieRepository;
import moviereview.service.AnalysisService;
import moviereview.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kray on 2017/6/7.
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    CountryRepository countryRepository;

    public List<CountryScoreInYearBean> getCountryScoreInYearOfCountry(int countryid) {
        String countryName = countryRepository.findCountryByCountryId(countryid);
        List<CountryScoreInYearBean> result = new ArrayList<CountryScoreInYearBean>();
        DecimalFormat df = new DecimalFormat("#.00");
        for (int year = 1970; year <= 2017; year++) {
            Double score = movieRepository.findCountryScoreInYear(countryid, year);
            if (score == null) {
//                scoreList.add(-1.0);
            } else {
                result.add(new CountryScoreInYearBean(countryName, year, Double.parseDouble(df.format(score))));
            }
        }
        return result;
    }

    public List<CountryCountBean> getCountryCountOfCountry(int countryid) {
        String countryName = countryRepository.findCountryByCountryId(countryid);
        List<CountryCountBean> result = new ArrayList<>();

        Integer biggerIMDB = movieRepository.findCountBiggerThanIMDB(countryid);
        Integer smallerIMDB = movieRepository.findCountSmallerThanIMDB(countryid);
        Integer biggerDouban = movieRepository.findCountBiggerThanDouban(countryid);
        Integer smallerDouban = movieRepository.findCountSmallerThanDouban(countryid);

        result.add(new CountryCountBean(countryName, "foreign", biggerIMDB, smallerIMDB));
        result.add(new CountryCountBean(countryName, "domestic", biggerDouban, smallerDouban));
        return result;
    }
}