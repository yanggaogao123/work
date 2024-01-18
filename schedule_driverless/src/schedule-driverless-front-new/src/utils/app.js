let App = {
    checkIsApp() {
        let app = window.AppInterface
        if (window.webkit) {
            if (window.webkit.messageHandlers) {
                // iOS端 
                app = window.webkit.messageHandlers.AppInterface
            }
        }
        this.app = app
        return app
    },

    appFun(params) {
        if (!!params.Func) {
            if (!!params.cbName) {
                window[params.cbName] = params.Func
            } else {
                window[params.action] = params.Func
            }
            if (!!params.action) {
                // iOS 
                if (this.app.postMessage) {
                    // 点调用ios方法，然后ios调用h5方法进行传值 
                    this.app.postMessage({
                        func: params.action,
                        body: params.data
                    })
                } else if (this.app[params.action]) {
                    // Android 
                    // 直接调用安卓方法即可拿到传值
                    if (!!params.data) {
                        return this.app[params.action](JSON.stringify(params.data))
                    } else {
                        return this.app[params.action]()
                    }
                } else {
                    console.log('调用该方法失败：' + params.action)
                }
            }
        }
    },
    
    interFace(params) {
        if (this.checkIsApp()) {
            return this.appFun(params)
        }
    }
}

export default App