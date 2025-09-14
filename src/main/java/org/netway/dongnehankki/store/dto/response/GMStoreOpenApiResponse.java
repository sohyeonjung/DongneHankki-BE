package org.netway.dongnehankki.store.dto.response;// StoreOpenApiResponse.java

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GMStoreOpenApiResponse {
	@JsonProperty("RegionMnyFacltStus")
	private List<RegionMnyFacltStus> regionMnyFacltStus;

	@Data
	public static class RegionMnyFacltStus {
		private List<Head> head;
		private List<Row> row;
	}

	@Data
	public static class Head {
		@JsonProperty("list_total_count")
		private Integer listTotalCount;
		@JsonProperty("RESULT")
		private Result result;
		@JsonProperty("api_version")
		private String apiVersion;
	}

	@Data
	public static class Result {
		@JsonProperty("CODE")
		private String code;
		@JsonProperty("MESSAGE")
		private String message;
	}

	@Data
	public static class Row {
		@JsonProperty("CMPNM_NM")
		private String cmpnmNm;
		@JsonProperty("INDUTYPE_NM")
		private String indutypeNm;
		@JsonProperty("REFINE_LOTNO_ADDR")
		private String refineLotnoAddr;
		@JsonProperty("REFINE_ROADNM_ADDR")
		private String refineRoadnmAddr;
		@JsonProperty("REFINE_ZIPNO")
		private String refineZipno;
		@JsonProperty("REFINE_WGS84_LOGT")
		private String refineWgs84Logt;
		@JsonProperty("REFINE_WGS84_LAT")
		private String refineWgs84Lat;
		@JsonProperty("SIGUN_NM")
		private String sigunNm;
		@JsonProperty("BIZREGNO")
		private String bizregno;
		@JsonProperty("INDUTYPE_CD")
		private String indutypeCd;
		@JsonProperty("FRCS_NO")
		private String frcsNo;
		@JsonProperty("LEAD_TAX_MAN_STATE")
		private String leadTaxManState;
		@JsonProperty("CLSBIZ_DAY")
		private String clsbizDay;
		@JsonProperty("LEAD_TAX_MAN_STATE_CD")
		private String leadTaxManStateCd;
	}
}