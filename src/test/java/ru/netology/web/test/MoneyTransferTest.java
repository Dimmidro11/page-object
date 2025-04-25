package ru.netology.web.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCard;
    private DataHelper.CardInfo secondCard;

    @BeforeEach
    void setup() {
        // Подготавливаем среду для тестирования - открываем необходимый ресурс,
        // авторизируемся, вводим код верификации, заводим вспомогательные переменные
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.
                validVerify(verificationCode);
        firstCard = DataHelper.getFirstCardInfo();
        secondCard = DataHelper.getSecondCardInfo();
    }

    @Test
    @DisplayName("Успешный перевод средств с первой карты на вторую")
    void shouldTransferFromFirstToSecond() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = Math.max(openingBalance1 / 100, 10);

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(secondCard);

        // Совершаем перевод с желаемой карты на выбранную
        dashboardPage = transferPage.confirmTransfer(transferAmount, firstCard.getNumber());

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1 - transferAmount, finalBalance1);
        assertEquals(openingBalance2 + transferAmount, finalBalance2);
    }

    @Test
    @DisplayName("Перевод с первой карту на первую")
    void shouldDisplayedErrorNotification() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = Math.max(openingBalance1 / 100, 10);

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(firstCard);

        // Совершаем перевод с желаемой карты на выбранную
        dashboardPage = transferPage.confirmTransfer(transferAmount, firstCard.getNumber());

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1, finalBalance1);
        assertEquals(openingBalance2, finalBalance2);
    }

    @Test
    @DisplayName("Перевод со второй карты на первую нулевой суммы - проверка на ошибку")
    void shouldNotTransferFromSecondToFirstAboveBalance() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = 0;

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(firstCard);

        // Совершаем перевод с желаемой карты на выбранную
        dashboardPage = transferPage.confirmTransfer(transferAmount, secondCard.getNumber());

        // Проверяем на ошибку
        transferPage.checkErrorTransfer("Ошибка!");

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1, finalBalance1);
        assertEquals(openingBalance2, finalBalance2);
    }

    @Test
    @DisplayName("Перевод со второй карты на первую минимальной суммы (1 руб.)")
    void shouldNotTransferFromSecondToFirstMinimalAmount1Rub() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = 1;

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(firstCard);

        // Совершаем перевод с желаемой карты на выбранную
        dashboardPage = transferPage.confirmTransfer(transferAmount, secondCard.getNumber());

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1 + transferAmount, finalBalance1);
        assertEquals(openingBalance2 - transferAmount, finalBalance2);
    }

    @Test
    @DisplayName("Отмена перевода со первой карты на вторую")
    void shouldCancelTransferFromFirstToSecond() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = Math.max(openingBalance1 / 100, 10);

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(secondCard);

        // Совершаем перевод с желаемой карты на выбранную
        transferPage.clearFieldsAndFillingAmount(transferAmount);
        transferPage.setCardFrom(firstCard.getNumber());
        dashboardPage = transferPage.cancelTransfer();

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1, finalBalance1);
        assertEquals(openingBalance2, finalBalance2);
    }

    @Test
    @DisplayName("Перевод с первой карты на вторую суммы большей, чем баланс карты")
    void shouldNotTransferFromFirstToSecondAboveBalance() {
        // Сохраняем начальные балансы карт в переменные и определяем сумму перевода
        var openingBalance1 = dashboardPage.getCardBalance(firstCard);
        var openingBalance2 = dashboardPage.getCardBalance(secondCard);
        var transferAmount = openingBalance1 * 2;

        // Выбираем карту для пополнения
        var transferPage = dashboardPage.selectCard(secondCard);

        // Совершаем перевод с желаемой карты на выбранную
        dashboardPage = transferPage.confirmTransfer(transferAmount, firstCard.getNumber());

        // Сохраняем конечные балансы карты в переменные
        var finalBalance1 = dashboardPage.getCardBalance(firstCard);
        var finalBalance2 = dashboardPage.getCardBalance(secondCard);

        // Сравниваем корректность балансов после перевода
        assertEquals(openingBalance1, finalBalance1);
        assertEquals(openingBalance2, finalBalance2);
    }
}

