<template>
    <div class="xxtMap">
        <div class="nearby">
            <header>
                <div id="container"></div>
            </header>
            <section>

            </section>
        </div>
        <div class="search">

        </div>
        <footer>
            <ul>
                <li class="blue">
                    <van-icon class="tabLogo" name="aim" />
                    <span>附近</span>
                </li>
                <li>
                    <van-icon class="tabLogo" name="search" />
                    <span>搜索</span>
                </li>
            </ul>
        </footer>
    </div>
</template>

<script>
    export default {
        name: 'xxtMap',
        data() {
            return {

            }
        },
        created() {

        },
        methods: {

        }
    }
</script>

<style lang="less">
    @blue: #24a1ee;
    @gray: #a1a1a1;
    @line: #e6e6e6;
    .xxtMap {
        height: 100%;
        position: relative;
        .nearby{
            height: calc(100% - 40px);
            header {
                #container {
                    width: 100%;
                    height: 280px;
                    background: red;
                }
            }
            section{
                width: 100%;
                height: calc(100% - 280px);
                background: blue;
            }
        }
        footer{
            position: fixed;
            width: 100%;
            height: 40px;
            box-sizing: border-box;
            border-top: 1px solid @line;
            left: 0;
            bottom: 0;
            ul{
                display: flex;
                li{
                    flex: 1;
                    text-align: center;
                    .tabLogo{
                        display: block;
                        font-size: 1.6rem;
                        margin-top: 4px;
                    }
                    span{
                        display: block;
                    }
                }
                .blue{
                    color: @blue;
                }
            }
        }
        
    }
</style>