package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import java.time.LocalDate;
import java.util.Calendar;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.screenshot;

public class TransferPage {

    private SelenideElement heading = $("h1");
    private SelenideElement amountField = $("[data-test-id='amount'] input");
    private SelenideElement fromField = $("[data-test-id='from'] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");

    public TransferPage() {
        heading.shouldBe(visible);
    }

    public void clearFieldsAndFillingAmount(int transferAmount) {
        amountField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        amountField.setValue(Integer.toString(transferAmount));
        fromField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }

    public DashboardPage confirmTransfer(int transferAmount, String cardNumber) {
        clearFieldsAndFillingAmount(transferAmount);
        setCardFrom(cardNumber);
        String pngFileName = screenshot("Scrn_transfer_inform_" + Calendar.getInstance().getTimeInMillis());
        transferButton.click();
        String pngFile = screenshot("Scrn_final_balance_" + Calendar.getInstance().getTimeInMillis());
        return new DashboardPage();
    }

    public DashboardPage cancelTransfer() {
        String pngFileName = screenshot("Scrn_transfer_inform_" + Calendar.getInstance().getTimeInMillis());
        cancelButton.click();
        String pngFile = screenshot("Scrn_final_balance_" + Calendar.getInstance().getTimeInMillis());
        return new DashboardPage();
    }

    public void setCardFrom(String cardNumber) {
        fromField.setValue(cardNumber);
    }

    public void checkErrorTransfer(String expectedMessage) {
        errorNotification.shouldBe(visible).shouldBe(text(expectedMessage));
    }
}
