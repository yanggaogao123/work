/**
 * 邮箱
 * @param {*} s
 */
export function isEmail (s) {
  return /^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 手机号码
 * @param {*} s
 */
export function isMobile (s) {
  // return /^1[0-9]{10}$/.test(s)
  return /^[1][3-9][0-9]{9}$/.test(s);
}

/**
 * 电话号码
 * @param {*} s
 */
export function isPhone (s) {
  // return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
  return /^0\d{2,3}-\d{7,8}$/.test(s);
}

/**
 * URL地址
 * @param {*} s
 */
export function isURL (s) {
  //  const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
  // return urlregex.test(s);
  return /^http[s]?:\/\/.*/.test(s)
}

/**
 * ip地址
 * @param {*} s
 */
export function isIP (s) {
  return /^(1-255).(0-255).(0-255).(0-255)$/.test(s)
}

/**
 * 合法文本校验
 * 测试规则：!/[]{}@#\$%\^&*|:<>除外
 * @param s
 */
export function isValidText(s) {
  return /^[^~!@#$%^&*()_\-+=<>?:"{}|,.\\/;'\\[\]·~！@#￥%……&*——\-+={}|？：；‘']*$/.test(s)
}

/*
* 只能输入数字
* */
export function isNumber (s) {
  return /^[0-9]{0,100}$/.test(s)
}

/*
* 只能输入大写字母和数字
* */
export function isCharAndNumber (s) {
  return /^[0-9A-Z]{1,10}$/.test(s)
}

/*
* 广州公交车车牌验证
* */
export function isPlatenum (s) {
  return /^[粤][A][0-9A-HJ-NP-Z]{5,6}$/.test(s)
}

// /**
//  * 是否合法车牌号
//  * @param s
//  * @returns {boolean}
//  */
// export function isPlateNum(s) {
//   return /^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[a-zA-Z](([DF]((?![IO])[a-zA-Z0-9](?![IO]))[0-9]{4})|([0-9]{5}[DF]))|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1})$/.test(s)
// }

/**
 *   //校验车牌号
 */
export const checkedPlatenum = (rule, value, callback)=>{
  var regex = new RegExp(
    /^[粤][A][0-9A-HJ-NP-Z]{5,6}$/
  )
  if(value && !regex.test(value)){
    callback(new Error('请输入正确的车牌号!'));
  }else {
    callback();
  }
}

export function validateRouteCode(rule, value, callback) {
  var reg = /^[A-Z0-9]{5}$/;
  if (value && !reg.test(value)) {
    callback('请输入正确的线路编码（只含数字和字母，长度为5）');
  } else {
    callback()
  }
}

/**
 * 是否手机号码或者固话
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
 export function validatePhoneTwo(rule, value, callback) {
  const reg = /^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/;
  if (value == '' || value == undefined || value == null) {
    callback();
  } else {
    if(rule.max){
      if(value.length>rule.max){
        callback(new Error('最大只能输入'+rule.max+'字符'))
      }else{
        callback();
      }
      return
    }
    if ((!reg.test(value)) && value != '') {
      callback(new Error('请输入正确的电话号码或者固话号码'));
    } else {
      callback();
    }
  }
}

/**
 * 是否固话
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
 export function validateTelphone(rule, value,callback) {
  const reg =/0\d{2,3}-\d{7,8}/;
  if(value==''||value==undefined||value==null){
    callback();
  }else {
    if ((!reg.test(value)) && value != '') {
      callback(new Error('请输入正确的固定电话）'));
    } else {
      callback();
    }
  }
}
/**
 * 是否手机号码
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validatePhone(rule, value,callback) {
  const reg =/^[1][3-9][0-9]{9}$/;
  if(value==''||value==undefined||value==null){
    callback();
  }else {
    if ((!reg.test(value)) && value != '') {
      callback(new Error('请输入正确的电话号码'));
    } else {
      callback();
    }
  }
}
/**
 * 是否身份证号码
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validateIdNo(rule, value,callback) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  if(value==''||value==undefined||value==null){
    callback();
  }else {
    if ((!reg.test(value)) && value != '') {
      callback(new Error('请输入正确的身份证号码'));
    } else {
      callback();
    }
  }
}
/**
 * 是否邮箱
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validateEMail(rule, value,callback) {
  const reg =/^([a-zA-Z0-9]+[-_\.]?)+@[a-zA-Z0-9]+\.[a-z]+$/;
  if(value==''||value==undefined||value==null){
    callback();
  }else{
    if (!reg.test(value)){
      callback(new Error('请输入正确的邮箱'));
    } else {
      callback();
    }
  }
}
/**
 * 验证内容是否包含英文数字以及下划线
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function isNumbersAndLetters(rule, value, callback) {
  const reg =/^[a-zA-Z0-9]+$/;
  if(value==''||value==undefined||value==null){
    callback();
  } else {
    if (!reg.test(value)){
      callback(new Error('只能输入数字和字母'));
    } else {
      callback();
    }
  }
}
/**
 * 验证内容是否包含英文数字以及下划线
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validatePassword(rule, value, callback) {
  const reg =/^[_a-zA-Z0-9]+$/;
  if(value==''||value==undefined||value==null){
    callback();
  } else {
    if (!reg.test(value)){
      callback(new Error('仅由英文字母，数字以及下划线组成'));
    } else {
      callback();
    }
  }
}

/**
 * 验证是否整数
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validateInteger(rule, value, callback) {
  if (!value) {
    return callback(new Error('输入不可以为空'));
  }
  setTimeout(() => {
    if (!Number(value)) {
      callback(new Error('请输入正整数'));
    } else {
      const re = /^[0-9]*[1-9][0-9]*$/;
      const rsCheck = re.test(value);
      if (!rsCheck) {
        callback(new Error('请输入正整数'));
      } else {
        callback();
      }
    }
  }, 0);
}

/**
 * 验证是否是[0-1]的小数
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export function validateDecimal(rule, value, callback) {
  if (!value) {
    return callback(new Error('输入不可以为空'));
  }
  setTimeout(() => {
    if (!Number(value)) {
      callback(new Error('请输入[0,1]之间的数字'));
    } else {
      if (value < 0 || value > 1) {
        callback(new Error('请输入[0,1]之间的数字'));
      } else {
        callback();
      }
    }
  }, 100);
}

/**
 * 纯数字校验
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export const validateNumber = (rule, value, callback) => {
  if(rule.required||value){
    if(!value){
      callback(new Error('必填项不能为空！'))
      console.log("必填项不能为空")
      return
    }
    if(rule.max){
      if(value.length>rule.max){
        callback(new Error('最大只能输入'+rule.max+'位数字'))
      }else{
        callback()
      }
      return
    }
    console.log('0000')
    ///^\d+$|^\d+[.]?\d+$/
    let numberReg = /^\d+$/
    if (value !== '') {
        if (!numberReg.test(value)) {
            callback(new Error('请输入数字'))
        } else {
            callback()
        }
    } else {
        callback(new Error('请输入值'))
    }
  }else{
    callback()
  }
}
/**
 * 最多两位位小数
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export const twoPoint = (rule, value, callback) => {
  console.log('twoPoint',rule.required)
  if(rule.required||value){
    if(!value){
      callback(new Error('必填项不能为空！'))
      console.log("必填项不能为空")
      return
    }
    if(rule.max){
      if(value.length>rule.max){
        callback(new Error('最大只能输入'+rule.max+'位字符'))
        console.log('最大只能输入'+rule.max+'位字符')
        return
      }
      if (!/^[0-9]+([.]{0,1}[0-9]{1,2})?$/.test(value)) {
        callback(new Error('最多两位小数！！！'));
      } else {
        callback();
      }
    }else{
      callback();
    }
  }else{
    callback();
  }
}

/**
 * 最多四位小数
 * @param {*} rule
 * @param {*} value
 * @param {*} callback
 */
export const fourPoint = (rule, value, callback) => {
  console.log('fourPoint',rule.required)
  if(rule.required||value){
    if(value!=0 && !value){
      callback(new Error('必填项不能为空！'))
      console.log("必填项不能为空")
      return
    }
    if(rule.max){
      if(value.length>rule.max){
        callback(new Error('最大只能输入'+rule.max+'位字符'))
        console.log('最大只能输入'+rule.max+'位字符')
        return
      }
      if (!/^[0-9]+([.]{0,1}[0-9]{1,4})?$/.test(value)) {
        callback(new Error('最多四位小数！！！'));
      } else {
        callback();
      }
    }else{
      callback();
    }
  }else{
    callback();
  }
}

/**
 * 验证文本合法格式
 * 测试规则：!/[]{}@#\$%\^&*|:<>除外
 * @param text
 * @param length
 */
export const validateText = (rule, value, callback)=>{
  var regex = new RegExp(/^[A-Za-z0-9]*$/)
  if (value && value.split(" ").join("").length<value.length) {
    callback(new Error('不能输入空格!'));
  } else if(value && !regex.test(value)){
    callback(new Error('请输入合法字符（只含数字和字母）!'));
  } else {
    callback();
  }
}

/**
 * 基础数据备注专用
 * 测试规则：!/[]{}@#\$%\^&*|:<>除外
 * @param text
 * @param length
 */
export const validateRemark = (rule, value, callback)=>{
  var regex = new RegExp(/^[^~@#$%^&*()_+=<>?"{}|,.\/;'\\[\]·~！@#￥%……&*+={}|？‘']*$/)
  if(value && !regex.test(value)){
    callback(new Error('请输入合法字符!'));
  } else {
    callback();
  }
}

/**
 * 合法url
 * @param {*} url
 */
export const validateURL= (rule, value, callback)=>{
  var urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
  if(value && !urlregex.test(value)){
    callback(new Error('请输入合法url!'));
  } else {
    callback();
  }
}
/**
 * 合法IP
 * @param {*} url
 */
export const validateIP= (rule, value, callback)=>{
  const urlregex = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
  if(value && !urlregex.test(value)){
    callback(new Error('请输入合法IP!'));
  } else {
    callback();
  }
}

// /**
//  * HH:mm 时间格式校验
//  */
// export  const validateHHmm =(value)=>{
//   var regex =/^(?:[01]\d|2[0-3])(?::[0-5]\d)$/;
//   return regex.test(value)
// }
// /**
//  * YYYY-MM-DD HH:mm:ss 日期格式校验
//  */
// export  const validateDate =(value)=>{
//   var regex =/^((\d{2}(([02468][048])|([13579][26]))[\-\/\s]?((((0?[13578])|(1[02]))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\-\/\s]?((0?[1-9])|([1-2][0-9])))))|(\d{2}(([02468][1235679])|([13579][01345789]))[\-\/\s]?((((0?[13578])|(1[02]))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\-\/\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\s((([0-1][0-9])|(2?[0-3]))\:([0-5]?[0-9])((\s)|(\:([0-5]?[0-9])))))?$/;
//   return regex.test(value)
// }

