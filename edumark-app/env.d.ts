/// <reference types="vite/client" />
/// <reference path="./node_modules/@dcloudio/uni-uts-v1/lib/tsconfig/hbuilderx/shim-uni.d.ts" />
/// <reference path="./node_modules/@dcloudio/uni-uts-v1/lib/tsconfig/hbuilderx/shim-dom.d.ts" />
/// <reference path="./node_modules/@dcloudio/uni-uts-v1/lib/tsconfig/hbuilderx/global.d.ts" />
/// <reference path="./node_modules/@dcloudio/uni-uts-v1/lib/tsconfig/types/dcloudio__uni-h5/types/index.d.ts" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'

  const component: DefineComponent<Record<string, never>, Record<string, never>, any>
  export default component
}
