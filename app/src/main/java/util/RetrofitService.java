package util;

import java.util.Map;

import kr.foryou.ddcheon.count.CountItem;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("api/get_count.php")
    Call<CountItem> getCountData(
      @FieldMap Map<String,String> option
    );

}
