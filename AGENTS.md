# Repository Guidelines

## Project Structure & Module Organization
This repository contains three active modules:

- `edumark-server/`: Spring Boot 3 backend. Java sources live in `src/main/java/com/edumark`, MyBatis XML in `src/main/resources/mapper`, and SQL bootstrap scripts in `src/main/resources/sql` and `src/main/resources/db`.
- `edumark-admin/`: Vue 3 + TypeScript admin console. Core code is under `src/`, with page views in `src/views`, shared API clients in `src/api`, layouts in `src/layouts`, and global styles in `src/assets/styles`.
- `edumark-app/`: UniApp mobile client for parents/students. Pages are under `src/pages`, shared state in `src/stores`, and static assets in `static/`.

Keep changes scoped to the owning module. Do not mix backend SQL, admin UI, and app logic in a single commit unless the feature truly spans all three.

## Build, Test, and Development Commands
- `cd edumark-server && mvn spring-boot:run`: run the backend locally.
- `cd edumark-server && mvn clean package -DskipTests`: build the backend JAR.
- `cd edumark-server && mvn test`: run backend tests.
- `cd edumark-admin && npm install && npm run dev`: start the admin console with Vite.
- `cd edumark-admin && npm run build`: type-check and build the admin console.
- `cd edumark-admin && npm run lint`: run ESLint with auto-fix.
- `cd edumark-app && npm install && npm run dev:h5`: run the UniApp client in H5 mode.
- `cd edumark-app && npm run build:h5`: build the H5 client.

## Coding Style & Naming Conventions
Use existing module conventions instead of introducing new ones. Vue and TypeScript files use 2-space indentation, `camelCase` for variables/functions, and `PascalCase` for components such as `RegionConfigDialog.vue`. Java uses 4-space indentation, `PascalCase` classes, `camelCase` methods, and package names under `com.edumark.<domain>`. Keep comments in Chinese to match the codebase.

## Testing Guidelines
Backend tests belong in `edumark-server/src/test/java` and should mirror the production package structure. Prefer focused service/controller tests with Spring Boot Test and security coverage for permission-sensitive endpoints. Frontend modules currently do not ship an automated test suite; at minimum, run `npm run build` or `npm run lint` and document manual verification for changed pages.

## Commit & Pull Request Guidelines
Recent history uses short Chinese summaries such as `优化`, `答题卡优化`, and `仲裁`. Follow that style, but make the scope clearer when possible, for example `考试模块优化` or `阅卷任务修复`. Pull requests should include: changed modules, business impact, database/config updates, verification commands, and screenshots for admin/app UI changes. Link related issues and call out any SQL scripts reviewers must apply.
