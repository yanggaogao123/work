<template>
  <div id="main">123</div>
</template>

<script>
// import { axios } from "@/utils/requestUtil";
import axios from "axios";
import {
  getAction,
  deleteAction,
  putAction,
  postAction,
  httpAction,
  realPostAction,
} from "@/api/manage";
export default {
  name: "testApi",
  data() {
    return {};
  },
  created() {
    const queryString = window.location.search;
    const searchParams = new URLSearchParams(queryString);
    const paramString = searchParams.get("paramString");

    console.log(paramString);

    // post请求url拼接字符串，拼接成/simulation/adrealInfo?paramString=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // let paramString =
    //   "0vgfjEqNVeNg4VySqF5Go9uNdrol6HLIceflwfS8LSHL024pPc1p3bCbZRiLb8CCh6JPCbfPB3jH/VKak6+0z3hhWXOrLWTM8MmD1+ABLk3fEOuPOCn/3G9lrCyspUd9V3VWLulyQLu2vu62i9bw8lmz0wy1PAU/yktF9jJU74n6+xdMqA0CYl+SzduOeyzkFpyFRH+F8n2eqbbW/GLPEtYO4Mh3ERHz9nOOGjnEARF0fa8xio9zZkwZDLIh5BtjPUcMdcUcd45k0AdgCA/J337lZDJeP5pMOw+X5VhFZAc=";

    const params = new URLSearchParams();
    params.append("paramString", paramString);
    console.log(params.toString());
    axios
      .post(
        `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        {
          routeId: "194",
          supportRouteId: "420",
          runDate: "2023-12-18 07:00:00",
        },
        {
          params,
        }
      )
      .then((res) => {
        console.log(res);
      });
  },
};
</script>

<style></style>
