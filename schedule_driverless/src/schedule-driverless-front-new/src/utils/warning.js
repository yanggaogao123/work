// const warningOk = function(msg){
//     Notify({
//         message: msg,
//         color: '#24a1ee',
//         background: '#ffe1e1'
//     });
// }
import Vue from 'vue';
import {
    Notify
} from 'vant';

Vue.use(Notify);

const warning = {
    warningOK(msg){
        Notify({
            message: msg,
            color: '#ad0000',
            background: '#24a1ee',
            duration: 2000,

        });
    },
    warningDanger(msg){
        Notify({
            type: 'danger',
            message: msg,
            duration: 2000,
        });
    },
}


export function warningOk(msg) {
        Notify({
            message: msg,
            color: '#24a1ee',
            background: '#ffe1e1'
        });
}
   
    // return a;

export function haha(){
    return '123';
}

export {warning};
    