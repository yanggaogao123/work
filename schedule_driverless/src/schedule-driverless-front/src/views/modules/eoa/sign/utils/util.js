import * as constant from './constant'

export function getEnumByValue(value, enumObject, allowNull = true) {
  for (let key in enumObject) {
    if (enumObject.hasOwnProperty(key)) {
      if (enumObject[key].value === value) {
        return enumObject[key]
      }
    }
  }
  return allowNull ? null : {}
}

function getByEnum(value, enumObject) {
  for (let key in enumObject) {
    if (enumObject.hasOwnProperty(key)) {
      if (enumObject[key].value === value) {
        return enumObject[key].text
      }
    }
  }
  return value
}

export function getSignToDayStatus(value) {
  return getByEnum(value, constant.SignToDayStatusEnum)
}

export function getSignStatus(value) {
  return getByEnum(value, constant.SignStatusEnum)
}

export function getSignType(value) {
  return getByEnum(value, constant.SignTypeEnum)
}

function getOptionsByEnum(value, enumObj) {
  return Object.keys(enumObj).map(key => ({ value: enumObj[key].value, label: enumObj[key].text }))
}


export function getOptionsSignStatus(value) {
  return getOptionsByEnum(value, constant.SignStatusEnum)
}

export function getOptionsSignType(value) {
  return getOptionsByEnum(value, constant.SignTypeEnum)
}