# Repository Guidelines

## 项目结构与模块组织
本仓库是 Gradle + Spring Boot 3（Java 17）项目，核心代码位于 `src/main/java/nl/hukss/randomdata/randomdata`，按 `controller`、`service`、`service/impl`、`mapper`、`entity`、`config`、`filter`、`util`、`vo` 分层。
- 配置与静态资源：`src/main/resources`（含 `application.yml`、`static/index.html`、`static/js/*`）
- 测试代码：`src/test/java`
- 测试配置：`src/test/resources/application-unit-test.yaml`
- 构建产物：`build/`（禁止手改）

## 开发与验证命令
在仓库根目录使用 Gradle Wrapper：
- `./gradlew.bat clean`：清理构建产物。
- `./gradlew.bat bootRun`：本地启动服务。
- `./gradlew.bat test`：运行全部测试（JUnit Platform）。
- `./gradlew.bat build`：执行编译、测试、打包。
- 执行 `./gradlew.bat test` 前需确保 Docker 可访问（本地或远端均可）。
- 如使用远端 Docker，可通过环境变量覆盖 `DOCKER_HOST`（项目测试任务已支持该变量）。

推荐提交流程：先 `test`，再 `build`，最后提交。

## 代码规范与命名
- Java 17，4 空格缩进，UTF-8。
- 包名全小写；类名 `PascalCase`；方法/字段 `camelCase`；常量 `UPPER_SNAKE_CASE`。
- 保持分层职责清晰：Controller 不直接访问数据库；Mapper 负责数据访问；Service 承载业务逻辑。
- 新增返回结构优先复用现有模式（如 `ResultData`、`ReturnCode`）。
- 不要在代码中编写啰嗦的注释

## 测试要求
- 测试框架：Spring Boot Test + JUnit Jupiter。
- 测试类以 `*Tests` 结尾，测试方法建议使用行为命名（如 `shouldXxxWhenYyy`）。
- 涉及数据库/中间件行为验证的集成测试必须使用 Testcontainers（尤其是 PostgreSQL）。
- 推荐使用 `@Testcontainers` + `@Container` + `@ServiceConnection`（Spring Boot 3）管理容器生命周期与连接注入。
- 集成测试禁止依赖本机已安装数据库实例；测试应可在空白环境直接运行。
- 涉及容器的测试需在测试代码内完成数据准备与清理（如建表、清表），避免依赖外部初始化脚本。
- 单元测试优先使用纯内存/Mock，不强制 Testcontainers。
- 仅需要真实基础设施行为验证的测试归类为 Testcontainers 集成测试。
- 修复缺陷时先补失败用例，再修复代码。
- 避免提交包含本机绝对路径的测试代码，优先使用测试资源或临时目录。

## 提交与 PR 规范
- 提交信息使用简洁祈使句，明确意图：`add jwt token filter`、`修复 company mapper 空指针`。
- 一次提交只做一类改动，避免把重构、功能、格式化混在一起。
- PR 至少包含：变更目的、关键修改点、测试结果（命令与结果）、关联 Issue/任务。
- 涉及 API/UI 行为变化时，附请求响应样例或截图。

## 变更边界与安全
- 不提交密钥、令牌、数据库密码等敏感信息到 `application.yml`。
- 凭据优先通过环境变量注入。
- 涉及认证/授权改动时，必须重点复核 `config/SecurityConfig.java` 与认证过滤器链。
- 未经说明不要顺手修改无关文件或重排大规模格式。
