package org.x3codes.pagingapp.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import org.x3codes.pagingapp.api.APIClient;
import org.x3codes.pagingapp.model.Movie;
import org.x3codes.pagingapp.model.MovieResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {


    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        try {
            int page = loadParams.getKey() != null ? loadParams.getKey() : 1;
            return APIClient.getInterface()
                    .getMoviesByPage(page)
                    .subscribeOn(Schedulers.io())
                    .map(MovieResponse::getMovies)
                    .map(movies -> toLoadResult(movies, page))
                    .onErrorReturn(LoadResult.Error::new);
        } catch(Exception e) {
            return Single.just(new LoadResult.Error(e));
        }
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> pagingState) {
        return 0;
    }


    private LoadResult<Integer, Movie> toLoadResult(List<Movie> movies, int page) {
        return new LoadResult.Page(movies, page == 1 ? null : page - 1, page + 1);
    }
}
