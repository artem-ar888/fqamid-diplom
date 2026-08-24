package ru.edu.qamid.utils.helpers;

import static ru.edu.qamid.utils.data.DataGenerator.transliterate;

import io.qameta.allure.kotlin.Allure;
import io.qameta.allure.kotlin.model.Status;
import io.qameta.allure.kotlin.model.StepResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import android.graphics.Bitmap;
import android.util.Log;
import androidx.test.core.app.DeviceCapture;



public class AllureHelper {
    public static void reportAllureStep(String stepName, Runnable espressoActions) {
        // 1. Создаем и запускаем шаг вручную
        String uuid = java.util.UUID.randomUUID().toString();
        StepResult stepResult = new StepResult();
        stepResult.setName(stepName);
        Allure.getLifecycle().startStep(uuid, stepResult);

        try {
            // 2. Выполняем действия Espresso
            espressoActions.run();

            // Если всё успешно, меняем статус напрямую в объекте
            stepResult.setStatus(Status.PASSED);
        } catch (Throwable throwable) {
            // 3. Если Espresso упал — красим шаг в КРАСНЫЙ
            stepResult.setStatus(Status.FAILED);

            // Делаем скриншот в момент падения
            takeScreenshotAndAttach();

            // Делаем лог ошибки раскрывающимся вложением
            attachErrorLog(throwable);

//            // (Опционально) Добавляем детали ошибки (стектрейс падения), чтобы видеть причину прямо в шаге
//            stepResult.setStatusDetails(io.qameta.allure.kotlin.util.ResultsUtils.getStatusDetails(throwable));
//            throw throwable; // Перевыбрасываем ошибку дальше, чтобы завалить сам тест

//            //  Создаем "чистую" ошибку, чтобы стереть гигантскую простыню снизу отчета
//            AssertionError cleanError = new AssertionError("Тест упал на шаге: «" + stepName + "»");
//            // Передаем пустой массив, чтобы убрать системный дублирующий стек-трейс
//            cleanError.setStackTrace(new StackTraceElement[0]);
//            throw cleanError;

//            throw new AssertionError("Тест упал на шаге: «" + stepName + "»", throwable);

            String consoleMessage = "Test failed at step: [" + transliterate(stepName) + "]";

            throw new AssertionError(consoleMessage, throwable);
        } finally {
            // 4. Обязательно закрываем шаг в любом случае
            Allure.getLifecycle().stopStep(uuid);
        }
    }

    private static final String TAG = "AllureAttachmentHelper";

    private static void takeScreenshotAndAttach() {
        try {
            // 1. Используем современный DeviceCapture вместо устаревшего класса Screenshot
            Bitmap bitmap = DeviceCapture.takeScreenshot();

            // 2. Сжимаем графику в поток байтов PNG
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] screenshotBytes = outputStream.toByteArray();

                // 3. Отправляем байты картинки напрямую в отчет Allure
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(screenshotBytes)) {
                    Allure.attachment(
                            "Скриншот в момент падения",
                            inputStream,
                            "image/png",
                            "png"
                    );
                }
            }
            Log.i(TAG, "Screenshot successfully generated and attached to Allure.");
        } catch (IOException e) {
            // Заменили printStackTrace() на надежный системный логгер Android
            Log.e(TAG, "Failed to create or attach screenshot to Allure.", e);
        } catch (Throwable t) {
            Log.e(TAG, "Critical error while attempting to capture screen.", t);
        }
    }

    private static void attachErrorLog(Throwable throwable) {
        try {
            // Превращаем весь стек ошибки (StackTrace) в обычную строку
            String stackTraceString = android.util.Log.getStackTraceString(throwable);

            // Переводим текст в байты
            byte[] logBytes = stackTraceString.getBytes(StandardCharsets.UTF_8);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(logBytes)) {
                // Отправляем в Allure как текстовый файл
                Allure.attachment(
                        "Лог ошибки (Стек-трейс)", // Имя вложения
                        inputStream,               // Поток байтов текста
                        "text/plain",              // MIME-тип: чистый текст
                        "txt"                      // Расширение
                );
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to attach error log.", t);
        }
    }
}
