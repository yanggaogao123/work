<template>
  <div class="container">
		<div class="top">
			<div class="code">标识码：{{stationInf.id}}</div>
			<div class="show-num">本标识码已解析<span>{{stationInf.analysis}}</span>次</div>
		</div>
		<div class="main">
			<div class="station-img">
				<img src="../../assets/xxt_industrial_internet/station.png" alt="">
				<div class="station-name" @click="jumpToStation">{{stationInf.stationName}}</div>
			</div>
			<div class="link-box">
				<div class="title">
					<div class="line"></div>
					<div class="txt">途经线路{{stationInf.routes.length}}条</div>
				</div>
				<div class="content">
					<div class="line-item" v-for="item in stationInf.routes" :key="item.routeId" @click="jumpToRouter(item.routeId)">{{item.routeName}}</div>
				</div>
			</div>
			<div class="inf">
				<div class="title">
					<div class="line"></div>
					<div class="txt">广州公交集团简介</div>
				</div>
				<div class="content">
					广州公交集团是广州市政府下属全资大型国有企业，由12家市属国有公共交通企事业单位于2017年12月组建成立。全新起航的广州公交集团始终保持为民初心，贯彻公交优先战略，以改革创新促高质量发展，努力打造便捷、安全、共享、绿色的公交服务系统,全面建成群众出行满意、行业发展可持续的城市公共交通体系，打造与广州国家中心城市定位相匹配的城市新名片。
					<img src="../../assets/xxt_industrial_internet/station.png" alt="">
				</div>
			</div>
		</div>
		<div class="footer">
			<img src="../../assets/xxt_industrial_internet/GCI_logo.png" alt="">
			广州交信投科技股份有限公司
		</div>
	</div>
</template>

<script>
import { postByStation } from '@/api/api_ii.js'
export default {
  components: {},
  props: {},
  data() {
    return {
			lines: [0,1,2,3,4,5,6,7],
			stationInf: {
				id: '',
				analysis: '',
				routes: [],
				stationId: '',
				stationName: '',
			},
    };
  },
  watch: {
  },
  computed: {
  },
  created() {
  },
  mounted() {
		let query = this.$route.query;
		let id = query.stationId;
		this.getStationInf(id)
  },
  methods: {
		getStationInf(id) {
			let sendData = {
				stationId: id ? id : '10000558'
			}
			postByStation(sendData).then((res) => {
				console.log('站点信息', res);
				this.stationInf = res
		 		document.title = this.stationInf.stationName
			})
		},
		jumpToBus() {
			this.$router.push({path: `/ii_bus`, query: {busId: 1}});
		},
		jumpToStation() {
			this.$router.push({path: `/ii_station`, query: {stationId: stationInf.id}});
		},
		jumpToRouter(id) {
			this.$router.push({path: `/ii_route`, query: {routeId: id}});
		}
  },
};
</script>

<style lang='less' scoped>
	.container{
		width: 100%;
		min-height: 100%;
		padding-bottom: 56px;
		background: #F3F3F3;
		position: relative;
		.top {
			padding: 16px 0 40px;
			border-bottom-right-radius: 50%;
			border-bottom-left-radius: 50%;
			background: linear-gradient(#506BFF, #3379FE);
			color: #fff;
			font-size: 13px;
			text-align: center;
			.code {
				margin-bottom: 6px;
			}
			.show-num {
				span {
					margin: 0 2px;
					color: #FFEC8F;
					font-weight: 600;
					font-size: 15px;
					display: inline-block;
				}
			}
		}
		.main {
			padding: 0 16px;
    	margin-top: -26px;
			>div {
				margin-bottom: 16px;
			}
			.station-img {
				width: 100%;
				height: 168px;
				position: relative;
				img {
					width: 100%;
					height: 100%;
					border-radius: 6px;
					display: block;
				}
				.station-name {
					width: 100%;
					background: rgba(0,0,0,.5);
					color: #fff;
					font-size: 24px;
					text-align: center;
					position: absolute;
					top: 50%;
					transform: translateY(-50%);
				}
			}
			.link-box {
				padding: 12px 16px;
				background: #fff;
				border-radius: 6px;
				box-shadow: 0 0 5px rgba(165, 178, 255, 0.53);
				>.title {
					margin-bottom: 12px;
					color: #506AFF;
					display: flex;
					align-items: center;
					.line {
						width: 4px;
						height: 16px;
						margin-right: 6px;
						background: #506AFF;
					}
					>.txt {
						font-size: 14px;
					}
				}
				.content {
					max-height: 72px;
					overflow-y: auto;
					display: flex;
					flex-flow: wrap;
					.line-item {
						width: 30%;
						height: 28px;
						line-height: 28px;
						margin: 0 1.66% 12px;
						border: 1px dashed #506AFF;
						border-radius: 4px;
						color: #506AFF;
						font-size: 15px;
						text-align: center;
					}
				}
			}
			.inf {
				padding: 12px 16px;
				background: #fff;
				border-radius: 6px;
				box-shadow: 0 0 5px rgba(165, 178, 255, 0.53);
				>.title {
					margin-bottom: 12px;
					color: #506AFF;
					display: flex;
					align-items: center;
					.line {
						width: 4px;
						height: 16px;
						margin-right: 6px;
						background: #506AFF;
					}
					>.txt {
						font-size: 14px;
					}
				}
				.content {
					color: #333333;
					font-size: 14px;
					img {
						width: 100%;
					}
				}
			}
		}
		.footer {
			padding: 16px 0;
			color: #585757;
			font-size: 16px;
			font-weight: 600;
			white-space: nowrap;
			text-align: center;
			position: absolute;
			bottom: 0;

			img {
				display: inline-block;
				width: 32%;
				max-height: 134px;
				margin-right: 8px;
			}
		}
	}
</style>