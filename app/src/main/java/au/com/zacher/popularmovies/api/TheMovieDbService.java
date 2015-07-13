/*
 * Copyright 2015 Brad Zacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.zacher.popularmovies.api;

import java.util.Map;

import au.com.zacher.popularmovies.api.results.CertificationResults;
import au.com.zacher.popularmovies.api.results.DatedPagedResults;
import au.com.zacher.popularmovies.api.results.GenreResults;
import au.com.zacher.popularmovies.api.results.PagedResults;
import au.com.zacher.popularmovies.api.results.RelatedPagedResults;
import au.com.zacher.popularmovies.api.results.RelatedResults;
import au.com.zacher.popularmovies.model.Company;
import au.com.zacher.popularmovies.model.Keyword;
import au.com.zacher.popularmovies.model.Movie;
import au.com.zacher.popularmovies.model.MovieVideo;
import au.com.zacher.popularmovies.model.MovieWithReleases;
import au.com.zacher.popularmovies.model.Review;
import au.com.zacher.popularmovies.model.SimpleMovie;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Brad on 10/07/2015.
 */
public interface TheMovieDbService {
    public interface ConfigurationService {
        @GET("/configuration")
        public void get(Callback<Configuration> callback);
    }

    public interface CertificationsService {
        @GET("/certification/movie/list")
        public void getMovieList(Callback<CertificationResults> callback);

        @GET("/certification/tv/list")
        public void getTvList(Callback<CertificationResults> callback);
    }

    // tODO: maybe include collections?
   /*public interface CollectionsService {
        @GET("collection/{id}")
        public void getCollection(@Path("id") String id, Callback<Object> callback);

        @GET("collection/{id}/images")
        public void getImages(@Path("id") String id, Callback<Object> callback);

        @GET("collection/{id}?append_to_response=images")
        public void getCollectionAndImages(@Path("id") String id, Callback<Object> callback);
    }*/

    public interface CompaniesService {
        @GET("/company/{id}")
        public void getCompany(@Path("id") String id, Callback<Company> callback);

        @GET("/company/{id}/movies")
        public void getCompanyMovies(@Path("id") String id, Callback<RelatedPagedResults<SimpleMovie>> callback);
        @GET("/company/{id}/movies")
        public void getCompanyMovies(@Path("id") String id, @Query("page") int pageNum, Callback<RelatedPagedResults<SimpleMovie>> callback);
    }

    public interface DiscoverService {
        @GET("/discover/movie")
        public void getMovieList(Callback<PagedResults<SimpleMovie>> callback);
        @GET("/discover/movie")
        public void getMovieList(@QueryMap Map<String, Object> map, Callback<PagedResults<SimpleMovie>> callback);

        //@GET("/discover/tv")
        //public void getTvList(Callback<Object> callback);
        //@GET("/discover/tv")
        //public void getTvList(@QueryMap Map<String, Object> map, Callback<Object> callback);
    }

    public interface GenresService {
        @GET("/genre/movie/list")
        public void getMovieList(Callback<GenreResults> callback);

        @GET("/genre/tv/list")
        public void getTvList(Callback<GenreResults> callback);

        @GET("/genre/{id}/movies")
        public void getMoviesForGenre(@Path("id") String id, Callback<RelatedPagedResults<SimpleMovie>> callback);
        @GET("/genre/{id}/movies")
        public void getMoviesForGenre(@Path("id") String id, @QueryMap Map<String, Object> map, Callback<RelatedPagedResults<SimpleMovie>> callback);
    }

    public interface KeywordService {
        @GET("/keyword/{id}")
        public void getKeyword(@Path("id") String id, Callback<Keyword> callback);

        @GET("/keyword/{id}/movies")
        public void getKeywordMovies(@Path("id") String id, Callback<RelatedPagedResults<SimpleMovie>> callback);
    }

    public interface MoviesService {
        @GET("/movie/{id}")
        public void getMovie(@Path("id") String id, Callback<Movie> callback);

        @GET("/movie/{id}?append_to_response=releases")
        public void getMovieWithReleases(@Path("id") String id, Callback<MovieWithReleases> callback);

        //@GET("/movie/{id}")
        //public void getMovieWith(@Path("id") String id, @Query("append_to_response") String commaSeparatedList, Callback<Object> callback);

        //@GET("/movie/{id}/alternative_titles")
        //public void getMovieAlternativeTitles(@Path("id") String id, Callback<Object> callback);

        //@GET("/movie/{id}/credits")
        //public void getMovieCredits(@Path("id") String id, Callback<Object> callback);

        //@GET("/movie/{id}/images")
        //public void getMovieImages(@Path("id") String id, Callback<Object> callback);

        //@GET("/movie/{id}/keywords")
        //public void getMovieKeywords(@Path("id") String id, Callback<Object> callback);

        //@GET("/movie/{id}/releases")
        //public void getMovieReleases(@Path("id") String id, Callback<Object> callback);

        @GET("/movie/{id}/videos")
        public void getMovieVideos(@Path("id") String id, Callback<RelatedResults<MovieVideo>> callback);

        //@GET("/movie/{id}/translations")
        //public void getMovieTranslations(@Path("id") String id, Callback<Object> callback);

        @GET("/movie/{id}/similar")
        public void getSimilarMovies(@Path("id") String id, Callback<PagedResults<SimpleMovie>> callback);

        @GET("/movie/{id}/reviews")
        public void getMovieReviews(@Path("id") String id, Callback<RelatedPagedResults<Review>> callback);

        //@GET("/movie/latest")
        //public void getLatest(Callback<Object> callback);

        @GET("/movie/now_playing")
        public void getNowPlaying(Callback<DatedPagedResults<SimpleMovie>> callback);
        @GET("/movie/now_playing")
        public void getNowPlaying(@Query("page") String pageNum, Callback<DatedPagedResults<SimpleMovie>> callback);

        @GET("/movie/popular")
        public void getPopular(Callback<PagedResults<SimpleMovie>> callback);
        @GET("/movie/popular")
        public void getPopular(@Query("page") String pageNum, Callback<PagedResults<SimpleMovie>> callback);

        @GET("/movie/top_rated")
        public void getTopRated(Callback<PagedResults<SimpleMovie>> callback);
        @GET("/movie/top_rated")
        public void getTopRated(@Query("page") String pageNum, Callback<PagedResults<SimpleMovie>> callback);

        @GET("/movie/upcoming")
        public void getUpcoming(Callback<DatedPagedResults<SimpleMovie>> callback);
        @GET("/movie/upcoming")
        public void getUpcoming(@Query("page") String pageNum, Callback<DatedPagedResults<SimpleMovie>> callback);
    }

    public interface ReviewsService {
        @GET("/review/{id}")
        public void getReview(@Path("id") String id, Callback<Review> callback);
    }

    // TODO: maybe include search?
    /*public interface SearchService {
        @GET("/search/company")
        public void company(@Query("query") String query, Callback<Object> callback);
        @GET("/search/company")
        public void company(@Query("query") String query, @Query("page") int pageNum, Callback<Object> callback);

        @GET("/search/collection")
        public void collection(@Query("query") String query, Callback<Object> callback);
        @GET("/search/collection")
        public void collection(@Query("query") String query, @Query("page") int pageNum, Callback<Object> callback);

        @GET("/search/keyword")
        public void keyword(@Query("query") String query, Callback<Object> callback);
        @GET("/search/keyword")
        public void keyword(@Query("query") String query, @Query("page") int pageNum, Callback<Object> callback);

        @GET("/search/movie")
        public void movie(@Query("query") String query, Callback<Object> callback);
        @GET("/search/movie")
        public void movie(@Query("query") String query, @QueryMap Map<String, Object> map, Callback<Object> callback);
        @GET("/search/movie")
        public void movie(@Query("query") String query, @Query("page") int pageNum, @QueryMap Map<String, Object> map, Callback<Object> callback);

        @GET("/search/tv")
        public void tv(@Query("query") String query, Callback<Object> callback);
        @GET("/search/tv")
        public void tv(@Query("query") String query, @QueryMap Map<String, Object> map, Callback<Object> callback);
        @GET("/search/tv")
        public void tv(@Query("query") String query, @Query("page") int pageNum, @QueryMap Map<String, Object> map, Callback<Object> callback);
    }*/

    // TODO: maybe extend to tv?
    /*public interface TVService {
        @GET("/tv/{id}")
        public void getTv(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}")
        //public void getTvWith(@Path("id") String id, @Query("append_to_response") String commaSeparatedList, Callback<Object> callback);

        //@GET("/tv/{id}/alternative_titles")
        //public void getTvAlternativeTitles(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}/credits")
        //public void getTvCredits(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}/images")
        //public void getTvImages(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}/keywords")
        //public void getTvKeywords(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}/releases")
        //public void getTvReleases(@Path("id") String id, Callback<Object> callback);

        @GET("/tv/{id}/videos")
        public void getTvVideos(@Path("id") String id, Callback<Object> callback);

        //@GET("/tv/{id}/translations")
        //public void getTvTranslations(@Path("id") String id, Callback<Object> callback);

        @GET("/tv/{id}/similar")
        public void getSimilarTv(@Path("id") String id, Callback<Object> callback);

        @GET("/tv/{id}/reviews")
        public void getTvReviews(@Path("id") String id, Callback<Object> callback);

        @GET("/tv/latest")
        public void getLatest(Callback<Object> callback);

        @GET("/tv/on_the_air")
        public void getOnTheAir(Callback<Object> callback);
        @GET("/tv/on_the_air")
        public void getOnTheAir(@Query("page") int pageNum, Callback<Object> callback);

        @GET("/tv/airing_today")
        public void getAiringToday(Callback<Object> callback);
        @GET("/tv/airing_today")
        public void getAiringToday(@Query("page") int pageNum, Callback<Object> callback);

        @GET("/tv/popular")
        public void getPopular(Callback<Object> callback);
        @GET("/tv/popular")
        public void getPopular(@Query("page") int pageNum, Callback<Object> callback);

        @GET("/tv/top_rated")
        public void getTopRated(Callback<Object> callback);
        @GET("/tv/top_rated")
        public void getTopRated(@Query("page") int pageNum, Callback<Object> callback);

        @GET("/tv/upcoming")
        public void getUpcoming(Callback<Object> callback);
        @GET("/tv/upcoming")
        public void getUpcoming(@Query("page") int pageNum, Callback<Object> callback);
    }*/
}
