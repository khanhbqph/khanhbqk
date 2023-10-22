package com.example.asmht.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.asmht.R;
import com.example.asmht.adapter.LoaiSpAdapter;
import com.example.asmht.adapter.SanPhamMoiAdapter;
import com.example.asmht.model.LoaiSp;
import com.example.asmht.model.SanPhamMoi;
import com.example.asmht.model.SanPhamMoiModel;
import com.example.asmht.retrofit.ApiBanHang;
import com.example.asmht.retrofit.RetrofitClient;
import com.example.asmht.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;

    DrawerLayout drawerLayout;

    LoaiSpAdapter loaiSpAdapter;

    List<LoaiSp>  mangloaisp;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);


        AnhXa();
        ActionBar();

        if(isConnected(this)){
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(), "Không có Internet, vui lòng kết nối lại!", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), LapTopActivity.class);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent dongho = new Intent(getApplicationContext(), DongHoActivity.class);
                        startActivity(dongho);
                        break;
                    case 4:
                        Intent maytinhbang = new Intent(getApplicationContext(), MayTinhBangActivity.class);
                        startActivity(maytinhbang);
                        break;
                    case 5:
                        Intent lienhe = new Intent(getApplicationContext(), LienHeActivity.class);
                        startActivity(lienhe);
                        break;
                    case 6:
                        Intent thongtin = new Intent(getApplicationContext(), ThongTinActivity.class);
                        startActivity(thongtin);
                        break;
                }
            }
        });

    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);

                            }

                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Không kết nối được với sever!" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }

                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSPModel -> {
                            if (loaiSPModel.isSuccess()){
                                //Toast.makeText(getApplicationContext(), loaiSPModel.getResult().get(0).getTensanpham(), Toast.LENGTH_SHORT).show();
                                mangloaisp = loaiSPModel.getResult();

                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));

    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://cdn.tgdd.vn/Files/2017/12/06/1047844/pk-12-800x450_800x450.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2023/07/03/1536736/thumb-090723-171119-800-resize.jpg");
        mangquangcao.add("data:image/jpeg;base64,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yU3ipljjeRtFRSx9gF6UTYkJSRHZqgYDbavkUqc5ChypRlV2Z0y3zXPORhcAi1jwoi26jMQVYKEV1Jy3ZSbXsG0vdbA2PG4FKFlrlr0LVUNvpzfw5BmykX3foMY1D+nwvIot6XHSnH20iu8bI4KNlHoHP+x4c7TWZBY+uooWWJXh8aUBVYu2kyJIQcrm2YZQosBcksRlGbm665tOq7eC20shVDG6sTlOi5Va8gsedf8Ak24A9FTRFlo70uIaCqT9OpnCiKS2VmY8y4QNkz+n6Nw1x6WnCvMLt9SLGOQnOVGXKRZmiCG5YAgiWPUX6TfpqSCw2l+X4/SimcTiM6RuLgMCbG1xw0Nri49Rr2gPIMc2UaLwHX1e2l+ft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70eft1L4/eiigDz9upfH70jz9jxVe4/evaKAZEoz7zImc83NY3st7W104nw6hS3Zdfw49Gzej+brPWdT30UUA3iQrgFkU7s3X0gLq1xcA2NrDj6+s0psQGNzGhN+OXW40Bve97V7RQCYZAFCBEsl8twWIPSbsSbm5ueJuacixGXgiC3Cy2tx6vae80UUA3BIEGVY0A16Drfjck6/GnI5h0Ig1vottdGv7bgdwoooCNi8YbLzV6eA9nrooooD/9k=");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2022/08/15/1456334/sale_1280x720-800-resize.jpg");
        mangquangcao.add("https://prices.vn/photos/media/1654672760-blobid1654672758033.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2018/08/30/1113568/29-giam-gia_800x432.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2020/08/30/1285521/3_800x450.png");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2015/02/10/608655/khai-truong-kinh-duong-vuong-02.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2015/05/29/648465/t6-400shop-800-350-10ngay.jpg");
        mangquangcao.add("https://cdn.tgdd.vn/Files/2022/12/13/1495244/thumb-2_1280x720-800-resize.jpg");
        mangquangcao.add("https://magiamgiathegioididong.com/wp-content/uploads/2022/02/Sinh-nhat-thegioididong-lich-sale-khuyen-mai-tgdd-ky-niem-sinh-nhat-hang-nam.jpg");
        for(int i =0; i<mangquangcao.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void AnhXa(){
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflippermanhinhchinh);
        recyclerViewManHinhChinh = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerLayout);

        //khoi tao list
        mangloaisp = new ArrayList<>();
        //khoi tao adapter

        //
        mangSpMoi = new ArrayList<>();
    }
    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//nho them quyen vao ko bi loi
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())){
            return true;
        }else {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}