package com.example.navegacioninferior;


import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import com.example.navegacioninferior.movie;
import com.example.navegacioninferior.MyApplicacion;
import com.example.navegacioninferior.R;



/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private static final String TAG=StoreFragment.class.getSimpleName();
    private static final String URL = "http://puntosingular.mx/json/movies2017.json";

    private RecyclerView recyclerView;
    private List<movie> movieList;
    private StoreAdapter mAdapter;

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(String param1,String param2){
        StoreFragment fragment= new StoreFragment();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_store,container,false);
        //return inflater.inflate(R.layout.fragment_store, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        movieList= new ArrayList<>();
        mAdapter = new StoreAdapter(getActivity(),movieList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(8),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fetchStoreItems();

        return view;
    }

    private void fetchStoreItems() {
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getActivity(), "Couldn't fetch the store items! please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                List<movie> items = new Gson().fromJson(response.toString(), new TypeToken<List<movie>>() {
                }.getType());
                movieList.clear();
                movieList.addAll(items);
                //refreshing
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error
                Log.e(TAG,"Error:"+error.getMessage());
                Toast.makeText(getActivity(),"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }

    });

        MyApplicacion.getInstance().addToRequestQueue(request);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        public GridSpacingItemDecoration(int spanCount, int spacing,boolean includeEdge) {
            this.spanCount=spanCount;
            this.spacing=spacing;
            this.includeEdge=includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position=parent.getChildAdapterPosition(view);//item position
            int column=position%spanCount;//item column
            if(includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;// spacing...
                outRect.right = (column + 1) * spacing / spanCount;//column...

                if (position < spanCount) {//top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;//item bottom
            }else{

                outRect.left=column*spacing/spanCount;//column*
                outRect.right=spacing-(column+1)*spacing/spanCount;
                if(position>=spanCount){
                    outRect.top=spacing;//item top
                 }
             }
          }
            //super.getItemOffsets(outRect, view, parent, state);
        }
    private int dpToPx(int dp){
        Resources r=getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }

     class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

        private Context context;
        private List<movie> movieList;

         public StoreAdapter(Context context, List<movie> movieList) {
             this.context = context;
             this.movieList = movieList;
         }

         public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView price;
            public TextView titulo;
            public ImageView thumbnail;

            public MyViewHolder(View view){
                super(view);
                titulo=view.findViewById(R.id.titulo);
                price=view.findViewById(R.id.price);
                thumbnail=view.findViewById(R.id.thumbnail);
            }

         }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

             View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item_row,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            final movie movie = movieList.get(position);

            holder.price.setText(movie.getPrice());
            holder.titulo.setText(movie.getTitle());

            Glide.with(context)
                    .load(movie.getImage())
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }


}
