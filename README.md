## Java 自动访问网站并程序化抓包

技术栈：`jnetpcap`  `selenium`

### 启动方式
1. 目前只支持 Chrome 浏览器
2. 基于 Chrome 的 selenium 浏览器驱动放在主目录下
   - 注意更改代码中驱动的文件路径（ `AbstractFlowCatch` 类下的 `initChromeDriver` 方法）
   - 注意驱动和 Chrome 浏览器的版本匹配（我的浏览器版本是 111.0.5563.65）

3. 更改代码中的网站 URL 和 端口（`AbstractFlowCatch` 类下的 `sendHttpRequest` 方法）
4. 运行启动类 FlowAnalysisApplication 即可
