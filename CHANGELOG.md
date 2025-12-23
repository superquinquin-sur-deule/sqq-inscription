# Changelog

## 1.2.0 (2025-12-23)

## What's Changed
* chore(main): release 1.1.1-SNAPSHOT by @lucas-dclrcq in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/34
* fix(deps): update quarkus.platform.version to v3.30.4 by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/26
* chore(deps): update dependency maven to v3.9.12 by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/25
* chore(deps): update dependency vite to v7.3.0 - autoclosed by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/23
* chore(deps): update actions/checkout action to v6 by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/22
* chore(deps): update docker/setup-buildx-action digest to 8d2750c by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/29
* chore(deps): update registry.access.redhat.com/ubi9/openjdk-21 docker tag to v1.24-2 by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/28
* chore(deps): update dependency vue to v3.5.26 - autoclosed by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/27
* fix(deps): update dependency com.stripe:stripe-java to v31.1.0 by @renovate[bot] in https://github.com/superquinquin-sur-deule/sqq-inscription/pull/24


**Full Changelog**: https://github.com/superquinquin-sur-deule/sqq-inscription/compare/v1.1.0...v1.2.0

## [1.1.0](https://github.com/superquinquin-sur-deule/sqq-inscription/compare/v1.0.0...v1.1.0) (2025-12-23)


### Features

* add a processing button on admin view ([9702d0b](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/9702d0b5ed8444c6e9b27f780a8bbba60e95953b))
* add endpoint to get one cooperateur ([35c1e5d](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/35c1e5dbf9b9db7ab1550a4eba67e2aaf635473f))
* add link to statuts ([127f875](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/127f875d8b94db50be6422d7e96403149b6fc3b1))
* add more binome info ([f6ae704](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/f6ae7042e8169994a8b0357b1cbdb956970b4052))
* add plugin to load umami script programmatically ([cecc35e](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/cecc35ec77636d2700eb497bcea1c6db3d5fd52e))
* add plugin to load umami script programmatically ([69381e4](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/69381e412f8d35c9d8156289f20494e2cc428d30))
* add soutenir superquinquin option ([9714adf](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/9714adfb73bfabcd8f7d4f71ed0c94b44edad8cc))
* add umami analytics ([56ea75d](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/56ea75d7ca2e4896d624c12db9d1512d2a2e30d6))
* add umami analytics ([926b71e](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/926b71e1f1e593bc5290d6de195d025fecbf894d))
* add updated and created dates on cooperateur ([bf9c6b8](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/bf9c6b8e6e88c910ce5e1a20c3162aade3decc1a))
* add vue router ([9219b1b](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/9219b1b487e40e3a8b5ecc6faf5f29d48e807321))
* allow to add additional social parts ([c52e72c](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/c52e72cb4b16135140c16ef7cf088caa69b14d6b))
* check every night if there are paid registrations to be sure ([f93e1ae](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/f93e1aea4fa1c8edad12e9937e9d17270fe5edcb))
* finalize payment process ([eb7e846](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/eb7e8469685a631559b0410bdd1331c79845f64f))
* implemenet stripe payment ([0ab1b86](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/0ab1b86ee069fc7e1eed09370c704f0aba4c6ed0))
* make total fields readonly ([be6e388](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/be6e388ad0add89fccc7d3e8fb8bef9ed6c5ac5e))
* move soutien at bottom of page ([5425c92](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/5425c9277981065e4286fa835cf47f9b06a137e4))
* only allow processing the cooperateur when he has paid ([16c3c0c](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/16c3c0c3f162facd54463e49c49be1228406446a))
* plug frontend to backend ([71b25d8](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/71b25d8372959a2e1e499a3b1b6ec0ef3566ed71))
* protect admin views with password ([a69a345](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/a69a34558cb017e2de02dff63a4abcaa020972bc))
* set default users for dev and test ([d81d794](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/d81d79430f5e9b92b76a57725be327f410ac84aa))
* simplify admin route ([615cda8](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/615cda81ab2a6cc7324be45c1e8a17432a8b64ff))
* store binome information ([f402448](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/f4024484ad3e6d42c3d61328b721b0dfc8441188))
* update successful registration view ([8d89baf](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/8d89bafdcb6065ad4fea34f67b2ecfcc67d0e640))
* use form submit instead of json post ([3712cbf](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/3712cbf552a2fb696b280489873479765d31292c))
* use tutoiement in registration page ([e30f833](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/e30f833b2dd02eae09b883e7bfda4f52fe26fdc7))


### Bug Fixes

* change wording in success page ([7411f71](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/7411f717a6d40a82394dc7361ba96c63af053886))
* **deps:** update dependency com.stripe:stripe-java to v31 ([5d986a9](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/5d986a9d4fa467e3b4a5dc1ca46a30849b966114))
* **deps:** update dependency io.quarkiverse.quinoa:quarkus-quinoa to v2.7.1 ([d8d9566](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/d8d95662b1b19ebe0e4d617572b304b4a01cd004))
* **deps:** update quarkus.platform.version to v3.30.3 ([d8d5ffc](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/d8d5ffcf70d3ec9e8b1566a9cc791d07ee93c355))
* show genre in admin view ([f6bbcf2](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/f6bbcf2ffb42130ff89e1233e46c3be8b2dfb5cf))
* store the binome genre ([995b289](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/995b289d81a91594fcfc501b598ced146197ad5a))
* update the frontend page title ([41baf8d](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/41baf8da3c6f27797b14621b6d357927addbf795))


### Other

* add renovate.json ([613f1a8](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/613f1a83993a163e8a831162915c5e0ded040c8f))
* create changelog md file ([4923336](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/4923336df243eadfb498bce2cf21d47d00886ca1))
* **deps:** update actions/checkout digest to 34e1148 ([b43f1de](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/b43f1de68fbd869a2901800bba7e24df630f520d))
* **deps:** update actions/setup-java action to v5 ([f473d27](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/f473d275f0108a3d43ed04ff5d3e2f979523e3dd))
* **deps:** update dependency @types/node to v24.10.4 ([8f56ca0](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/8f56ca0c6da0cb3d0fcf4257eb33be7e4ff5b1a8))
* **deps:** update dependency @vitejs/plugin-vue to v6.0.3 ([de75475](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/de7547507933ea4a16b3b6daefb35ae1c34e4467))
* **deps:** update dependency orval to v7.17.2 ([2f780ab](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/2f780abc5ee59061897c821bae6da0a5b539688b))
* **deps:** update dependency prettier to v3.7.4 ([2095f8d](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/2095f8d874cff2dc3a92c4858f80fc086ba68eb3))
* **deps:** update dependency vite to v7.2.7 ([03ffede](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/03ffede137d42b3b5818751081ac325673aa3e01))
* **deps:** update dependency vue to v3.5.25 ([5f71cad](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/5f71cad89072645c7d4114fa90bbfc3f71986570))
* **deps:** update dependency vue-router to v4.6.4 ([0ccaace](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/0ccaace6764148138a323527497cc1b58753eaac))
* **deps:** update dependency vue-tsc to v3.1.8 ([a9c8b5f](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/a9c8b5fa2fafeedcbab87b53b840c4e55c1925eb))
* **deps:** update dependency vue-tsc to v3.2.1 ([885bf9a](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/885bf9ac629772f1bcdd974d9cfbc781107b4ee8))
* **deps:** update docker/build-push-action digest to 2634353 ([41f6011](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/41f6011e7d80cfb8b6e0766844af0c41377fd6bc))
* **deps:** update docker/login-action digest to 5e57cd1 ([cbb9f72](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/cbb9f72cfd164ff7748faec15dc0056cfcd6221c))
* **deps:** update docker/setup-buildx-action digest to e468171 ([91ba862](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/91ba862695d01ad46240ad1d183089119e299d28))
* **deps:** update docker/setup-qemu-action digest to c7c5346 ([1a867ad](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/1a867adf9109b5ecac9da336d58cc58dfc64fdf1))
* **deps:** update registry.access.redhat.com/ubi9/openjdk-21 docker tag to v1.23-6.1764765565 ([d7db4f5](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/d7db4f57e8239409568d5d918ad4171f396026ba))
* **deps:** update registry.access.redhat.com/ubi9/ubi-minimal docker tag to v9.7-1764794109 ([805f11b](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/805f11bf7caa97669ada9c673efe8b8f4193a743))
* initial commit ([2da8949](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/2da8949326b89b35235e165ebaf0ec0bb9ad016f))
* **main:** release 1.0.1-SNAPSHOT ([863f041](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/863f04178f94a9dbbb638a7f1560a56b85edbf5b))


### Docs

* add env vars to readme ([5300fab](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/5300fabf93d6fcbed67537fc369adc7c0a99a797))


### Refactoring

* simplify parts logic ([263e326](https://github.com/superquinquin-sur-deule/sqq-inscription/commit/263e3269e09eeb5fcb2e73cd24adbb84eb930144))
