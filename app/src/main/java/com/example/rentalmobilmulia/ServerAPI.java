package com.example.rentalmobilmulia;

import com.example.rentalmobilmulia.model.KetersediaanResponse;
import com.example.rentalmobilmulia.model.MobilDetailResponse;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.model.MobilResponse;
import com.example.rentalmobilmulia.model.PesananModel;
import com.example.rentalmobilmulia.model.ResponseSewa;
import com.example.rentalmobilmulia.ui.beranda.BerandaMobil;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerAPI {

    // ================== MOBIL ==================
    @GET("getRekomendasiMobil.php")
    Call<List<MobilModel>> getRekomendasiMobil();

    @GET("get_mobil.php")
    Call<MobilResponse> getListMobil();


    @GET("get_detail_mobil.php")
    Call<MobilDetailResponse> getDetailMobil(@Query("id_mobil") int id);

    // ================== SEWA MOBIL ==================
    @GET("cek_ketersediaan.php")
    Call<KetersediaanResponse> cekKetersediaan(
            @Query("id_mobil") int idMobil,
            @Query("tanggal_mulai") String mulai,
            @Query("tanggal_selesai") String selesai
    );

    @FormUrlEncoded
    @POST("post_sewa.php")
    Call<ResponseSewa> postSewa(
            @Field("email") String email,
            @Field("id_mobil") int idMobil,
            @Field("tanggal_mulai") String tglMulai,
            @Field("tanggal_selesai") String tglSelesai,
            @Field("metode_pickup") String pickup,
            @Field("driver") String driver,
            @Field("total_harga") double total
    );


    @Multipart
    @POST("upload_bukti.php")
    Call<ResponseSewa> uploadBuktiBayar(
            @Part("kode_booking") RequestBody kodeBooking,
            @Part MultipartBody.Part bukti
    );


    // ================== AUTH ==================
// ================== PROFILE ==================
    @FormUrlEncoded
    @POST("login_user.php")
    Call<ResponseBody> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("getProfile.php")
    Call<ResponseBody> getProfile(@Field("email") String email);

    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<ResponseBody> updateProfile(
            @Field("nama_user") String nama,
            @Field("alamat") String alamat,
            @Field("telp") String telp,
            @Field("email") String email
    );

    @Multipart
    @POST("upload_image.php")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part imageupload,
            @Part("email") RequestBody email
    );


    @Multipart
    @POST("register_user.php")
    Call<ResponseBody> registerWithFile(
            @Part("nama_user") RequestBody nama,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("telp") RequestBody telp,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part ktp,
            @Part MultipartBody.Part kk
    );

    @FormUrlEncoded
    @POST("change_password.php")
    Call<ResponseBody> changePassword(
            @Field("email") String email,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword
    );


    @GET("get_pesanan.php")
    Call<List<PesananModel>> getPesanan(
            @Query("id_user") String idUser,
            @Query("status") String status
    );

}
