import React from 'react';

import {connect} from 'dva';

import MainLayout from '../MainLayout/MainLayout';
import Banner from '../MainLayout/Banner';

import ScorePyramidChart from '../Analysis/ScorePyramidChart';

import GenreScoreBarChart from '../Analysis/GenreScoreBarChart';
import GenreRingChart from '../Analysis/GenreRingChart';
import GenreLineChart from '../Analysis/GenreLineChart';
import GenreBarChart from '../Analysis/GenreBarChart';

import CountryYearScoreChart from '../Analysis/CountryYearScoreChart';
import CountryScoreBarChart from '../Analysis/CountryScoreBarChart';

import CountryGenreSankeyChart from '../Analysis/CountryGenreSankeyChart';

import styles from './AnalysisPage.css';

function AnalysisDataPage({location, quantityInGenre, genreQuantityScoreInYear}) {

    return (
        <div className={styles.normal}>


            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Score Pyramid</h3>
                </div>
                <ScorePyramidChart />
            </div>

            // no
            {/*<div className={styles.part}>*/}
            {/*<div className={styles.title}>*/}
            {/*<h3>Genre Quantity</h3>*/}
            {/*</div>*/}
            {/*{*/}
            {/*quantityInGenre ?*/}
            {/*<GenreRingChart data={quantityInGenre}/> :*/}
            {/*null*/}
            {/*}*/}
            {/*</div>*/}
            //


            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Genre Score Range</h3>
                </div>
                <GenreBarChart />
            </div>

            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Genre Score Count</h3>
                </div>
                <GenreScoreBarChart />
            </div>

            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Genre Count And Score In Year</h3>
                </div>
                {
                    genreQuantityScoreInYear ?
                        <GenreLineChart data={genreQuantityScoreInYear}/> :
                        null
                }
            </div>


            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Country Average Score In Year</h3>
                </div>
                <CountryYearScoreChart />
            </div>

            <div className={styles.part}>
                <div className={styles.title}>
                    <h3>Country Score Count</h3>
                </div>
                <CountryScoreBarChart />
            </div>

            // no
            {/*<div className={styles.part}>*/}
            {/*<div className={styles.title}>*/}
            {/*<h3>Genre Country Relationship</h3>*/}
            {/*</div>*/}
            {/*<CountryGenreSankeyChart />*/}
            {/*</div>*/}
            //

        </div>

    );
}


function mapStateToProps(state) {
    const analysis = state.analysis;
    return {
        quantityInGenre: analysis.quantityInGenre,
        genreQuantityScoreInYear: analysis.genreQuantityScoreInYear
    };
}

export default connect(mapStateToProps)(AnalysisDataPage);
