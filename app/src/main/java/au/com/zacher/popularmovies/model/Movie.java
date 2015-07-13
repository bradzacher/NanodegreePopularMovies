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

package au.com.zacher.popularmovies.model;

/**
 * Created by Brad on 11/07/2015.
 */
public class Movie extends MovieBase {
    public Collection belongs_to_collection;
    public int budget;
    public Genre[] genres;
    public String homepage;
    public String imdb_id;
    public SimpleCompany[] production_companies;
    public Country[] production_countries;
    public int revenue;
    public int runtime;
    public Language[] spoken_languages;
    public String status;
    public String tagline;
}
