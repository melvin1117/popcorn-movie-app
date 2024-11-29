package com.smfelix.movieapp.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.smfelix.movieapp.R
import com.smfelix.movieapp.ui.components.MovieDetailsComposable
import com.smfelix.movieapp.ui.viewmodels.MovieViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MovieViewPager(selectedMovieId: Long, viewModel: MovieViewModel) {
    val movies = viewModel.movieList.value


    if (movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No movies available", color = Color.White)
        }
        return
    }

    val initialPage = movies.indexOfFirst { movie -> movie.id == selectedMovieId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()

    Column {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 8.dp,
            containerColor = Color.Transparent,
            contentColor = colorResource(id = R.color.white),
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(4.dp)
                        .padding(horizontal = 28.dp)
                        .background(
                            color = colorResource(id = R.color.accent),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            },
            divider = {},
        ) {
            movies.forEachIndexed { index, movie ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    selectedContentColor = colorResource(id = R.color.accent),
                    unselectedContentColor = colorResource(id = R.color.lightGray),
                    text = {
                        Text(movie.title)
                    }
                )
            }
        }

        HorizontalPager(
            count = movies.size,
            state = pagerState,
            modifier = Modifier.fillMaxHeight()
        ) { page ->
            MovieDetailsComposable(movie = movies[page])
        }
    }
}
