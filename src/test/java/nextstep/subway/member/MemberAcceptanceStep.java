package nextstep.subway.member;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberAcceptanceStep {
    public static final String MEMBERS = "/members";
    public static final String MEMBERS_ME = "/members/me";
    public static final String LOCATION = "Location";
    public static final String AUTHORIZATION = "Authorization";

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().post(MEMBERS)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_등록되어_있음(String email, String password, Integer age) {
        return 회원_생성을_요청(email, password, age);
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header(LOCATION);

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header(LOCATION);
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_정보_조회_요청(String token) {
        return RestAssured
                .given().log().all()
                .header(AUTHORIZATION, makeBearerToken(token))
                .when().get(MEMBERS_ME)
                .then().log().all().extract();
    }

    private static String makeBearerToken(String token){
        return "Bearer " + token;
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header(LOCATION);
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 토큰_검증_실패됨(ExtractableResponse<Response> 나의_정보_조회_요청_실패_결과) {
        assertThat(나의_정보_조회_요청_실패_결과.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    public static void 나의_정보_응답됨(ExtractableResponse<Response> 나의_정보_조회_요청_결과) {
        assertThat(나의_정보_조회_요청_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
