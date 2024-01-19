// const path = require('path')

// function resolve(dir) {
//     return path.join(__dirname, dir)
// }

module.exports = {
  publicPath: process.env.VUE_APP_PUBLISH_PATH, //发布目录，默认必须填写'/'，如果要发布二级目录可以参考填写'/bar/'
  outputDir:
    "dist" + "/" + process.env.VUE_APP_ENV + process.env.VUE_APP_PUBLISH_PATH, //分不同环境打包到不同目录
  assetsDir: "static",
  productionSourceMap: false, // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  lintOnSave: process.env.NODE_ENV === "development",
  devServer: {
    port: 3000,
    proxy: {
      [process.env.VUE_APP_BASE_API]: {
        target: process.env.VUE_APP_DOMAIN_URL,
      },
      [process.env.VUE_APP_WXXT_API]: {
        target: process.env.VUE_APP_DOMAIN_URL,
      },
      [process.env.VUE_APP_LIST_API]: {
        target: process.env.VUE_APP_LIST_URL,
      },
      [process.env.VUE_APP_BUS_API]: {
        target: process.env.VUE_APP_BUS_URL,
      },
    },
  },
};
