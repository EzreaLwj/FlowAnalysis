package com.ezreal.flow;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFlowCatch {

    private ChromeDriver chromeDriver;


    protected abstract void beginFlowCatch();

    protected abstract void endFlowCatch();

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 15, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 配置 ChromeDriver
     */
    private void initChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        // 禁止 https 提示
        options.addArguments("--ignore-certificate-errors");
        // 禁用缓存
        options.addArguments("--disable-cache");
        DesiredCapabilities chrome = DesiredCapabilities.chrome();
        chrome.setCapability(ChromeOptions.CAPABILITY, options);
        chrome.setCapability("acceptInsecureCerts", true);
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        chromeDriver = new ChromeDriver(chrome);
    }

    /**
     * 利用浏览器模拟访问网页
     */
    private void sendHttpRequest() {

        final String baseUrl = "https://lcoalhost:port/pages/page%s.html";

        initChromeDriver();

        // 每一个网站访问 100 次
        for(int i = 1; i <= 20; i++) {
            int cnt = 0;
            String url = String.format(baseUrl, i);
            System.out.printf("访问第%s个网页%n", i);
            while (cnt < 100) {
                chromeDriver.get(url);

                try {
                    // 暂时休眠 2 秒
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start() {
        // 启动抓包
        beginFlowCatch();

        // 发送请求
        sendHttpRequest();

        // 关闭抓包
        endFlowCatch();
    }
}
