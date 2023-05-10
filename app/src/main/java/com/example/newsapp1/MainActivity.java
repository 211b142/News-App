package com.example.newsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface {
    //
    private RecyclerView newsRV,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles>articlesArrayList;
    private ArrayList<CategoryRVModal> categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRV = findViewById(R.id.idRVNews);
        categoryRV=findViewById(R.id.idRVCategories);
        loadingPB=findViewById(R.id.idPBLoading);
        articlesArrayList=new ArrayList<>();
        categoryRVModalArrayList=new ArrayList<>();
        newsRVAdapter=new NewsRVAdapter(articlesArrayList,this);
        categoryRVAdapter=new CategoryRVAdapter(categoryRVModalArrayList,this,this::onCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);
        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();
    }
    private void getCategories(){
        categoryRVModalArrayList.add(new CategoryRVModal("All","https://cdn.pixabay.com/photo/2014/08/24/19/01/apps-426559_960_720.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("Technology","https://media.istockphoto.com/id/1408387701/photo/social-media-marketing-digitally-generated-image-engagement.jpg?s=2048x2048&w=is&k=20&c=Gfl47p22O1FSu9KzcJXNLSkZ91W-ML8NTkOG3UkCw2g="));
        categoryRVModalArrayList.add(new CategoryRVModal("Science","https://cdn.pixabay.com/photo/2018/07/15/10/44/dna-3539309_640.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("Sports","https://cdn.pixabay.com/photo/2013/04/12/06/03/darts-102919__340.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("General","https://cdn.pixabay.com/photo/2023/03/20/13/48/ai-generated-7864776_640.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("Business","https://cdn.pixabay.com/photo/2016/04/20/08/21/entrepreneur-1340649_640.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("Entertainment","https://cdn.pixabay.com/photo/2017/11/24/10/43/ticket-2974645_640.jpg"));
        categoryRVModalArrayList.add(new CategoryRVModal("Health","https://cdn.pixabay.com/photo/2016/10/18/08/52/blood-pressure-monitor-1749577_640.jpg"));
        categoryRVAdapter.notifyDataSetChanged();

    }
    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryURL="https://newsapi.org/v2/top-headlines?country=in&category=" + category +" &apiKey=89bc467f806443e59dabfd3cdd72e9f4";
        String url="https://newsapi.org/v2/top-headlines?country=in&excludeDomain=stackoverflow.com&sortBy=publishedAt&language=en&category=science&apiKey=89bc467f806443e59dabfd3cdd72e9f4";
        String BASE_URL= "https://newsapi.org/";
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI= retrofit.create(RetrofitAPI.class);
       Call<NewsModal> call;
       if(category.equals("All")){
           call= retrofitAPI.getAllNews(url);
       }
       else{
           call= retrofitAPI.getNewsByCategory(categoryURL);
       }
       call.enqueue(new Callback<NewsModal>() {
           @Override
           public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
               NewsModal newsmodal= response.body();
               loadingPB.setVisibility(View.GONE);
               ArrayList<Articles> articles= newsmodal.getArticles();
               for(int i=0;i< articles.size();i++){
                   articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(),articles.get(i).getUrlToImage(),articles.get(i).getUrl(),articles.get(i).getContent()));

               }
               newsRVAdapter.notifyDataSetChanged();
           }

           @Override
           public void onFailure(Call<NewsModal> call, Throwable t) {
               Toast.makeText(MainActivity.this,"Fail to get news", Toast.LENGTH_SHORT).show();
           }
       });
    }
    @Override
    public void onCategoryClick(int position) {
        String category= categoryRVModalArrayList.get(position).getCategory();
        getNews(category);
    }
}