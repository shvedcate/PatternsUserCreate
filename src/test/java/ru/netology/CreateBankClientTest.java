package ru.netology;

import com.codeborne.selenide.SelenideElement;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.RegistrationDto;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class CreateBankClientTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(new RegistrationDto("vasya", "password", "active")) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    SelenideElement form = $("form");
    SelenideElement loginInput = form.$("[data-test-id=login] input");
    SelenideElement passwordInput = form.$("[data-test-id=password] input");
    SelenideElement button = $(byText("Продолжить"));
    SelenideElement personPage = $(withText("Личный кабинет"));
    SelenideElement errorNotification = $("[data-test-id='error-notification']");

    @BeforeEach
    void openHost() {
        open("http://localhost:9999");
    }


    @Test
    void personPageOpenWithValidLoginAndPassword() {

        loginInput.setValue("vasya");
        passwordInput.setValue("password");
        button.click();
        personPage.waitUntil(exist, 5000);
    }
    @Test
    void errorNotificationExistsWithValidLoginAndInvalidPassword() {

        loginInput.setValue("vasya");
        passwordInput.setValue("123456");
        button.click();
        errorNotification.waitUntil(exist, 5000);
    }
    @Test
    void errorNotificationExistsWithInvalidLoginAndValidPassword() {

        loginInput.setValue("petya");
        passwordInput.setValue("password");
        button.click();
        errorNotification.waitUntil(exist, 5000);
    }
}
