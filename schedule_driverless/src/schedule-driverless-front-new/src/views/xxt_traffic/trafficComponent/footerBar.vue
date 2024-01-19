<template>
    <ul class="footer-nav" id="footer-nav">
        <li v-for="(item,i) in nav" @click="tab(i)" :class="choice == i?'pick':''">
            <span class="iconfont" :class="item.icon"></span>
            <p>{{item.name}}</p>
        </li>
    </ul>
</template>

<script>
    export default {
        name: 'footer-nav',
        props: ['pick'],
        data() {
            return {
                nav: [{
                        icon: 'icon-quanshi',
                        name: '全市概况',
                    },
                    {
                        icon: 'icon-quyuguanli',
                        name: '区域管理',
                    },
                    {
                        icon: 'icon-jiaotongtongdao',
                        name: '交通通道',
                    },
                ],
                choice: 0,
            }
        },
        created(){
            this.dataInit();
        },
        methods:{
            dataInit(){
                this.choice = this.pick;
            },
            tab(i){
                switch (i) {
                    case 0:
                        this.$router.push({
                            name: 'trafficCity'
                        })
                        break;
                    case 1:
                        this.$router.push({
                            name: 'trafficArea'
                        })
                        break;
                    case 2:
                        this.$router.push({
                            name: 'trafficAccess'
                        })
                        break;
                    default:
                        break;
                }
            }
        }
    }
</script>

<style lang="less" scoped>
    @import "../../../assets/font-traffic/iconfont.css";
    .footer-nav{
                width: 100%;
                height: 50px;
                box-sizing: border-box;
                background: #fff;
                border-top: 1px solid #ccc;
                display: flex;
                margin: 0;
                li{
                    flex: 1;
                    text-align: center;
                    .iconfont{
                        font-size: 18px;
                    }
                    p{
                        margin: 0;
                        font-size: 1.4rem;
                    }
                }
                .pick{
                    color: #FF6701;
                }
            }
</style>