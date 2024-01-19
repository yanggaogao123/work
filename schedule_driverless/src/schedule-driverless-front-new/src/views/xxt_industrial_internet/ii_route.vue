<template>
	<div class="container">
		<div class="top">
			<div class="code">标识码：{{routeInf.id}}</div>
			<div class="show-num">本标识码已解析<span>{{routeInf.analysis}}</span>次</div>
		</div>
		<div class="main">
			<div class="route-from-to">
				<div class="from-to">
					<div class="from" v-if="routeInf.fromStationName.length < 7">{{routeInf.fromStationName ? routeInf.fromStationName : '暂无'}}</div>
					<marquee class="from" v-else>{{routeInf.fromStationName ? routeInf.fromStationName : '暂无'}}</marquee>
					<img class="arrow" src="../../assets/xxt_industrial_internet/arrow.png" />
					<div class="to" v-if="routeInf.lastStationName.length < 7">{{routeInf.lastStationName ? routeInf.lastStationName : '暂无'}}</div>
					<marquee class="from" v-else>{{routeInf.lastStationName ? routeInf.lastStationName : '暂无'}}</marquee>
				</div>
				<div class="time">
					<div class="l">{{routeInf.upFirstTime}} -- {{routeInf.upLastTime}}</div>
					<!-- 环线处理 -->
					<div class="r" v-if="routeInf.fromStationName == routeInf.lastStationName">{{routeInf.upFirstTime}}--{{routeInf.upLastTime}}</div>
					<div class="r" v-else>{{routeInf.downFirstTime}} -- {{routeInf.downLastTime}}</div>
				</div>
				<div class="other">
					<div class="l">票价{{routeInf.ticket}}元</div>
					<div class="r">{{routeInf.organName}}</div>
				</div>
			</div>
			<div class="link-box">
				<div class="title">
					<div class="line"></div>
					<div class="txt">途经车辆（{{routeInf.buses.length}}辆）</div>
				</div>
				<div class="content">
					<div class="line-item" v-for="item in routeInf.buses" :key="item.busId" @click="jumpToBus(item.busId)">{{item.numberPlate}}</div>
				</div>
			</div>
			<div class="route-station">
				<div class="title">
					<div class="line"></div>
					<div class="txt">线路站点</div>
					<!-- <div class="station" :class="{'choose': showContent == 1,}" @click="showContentChange(1)">
						线路站点<span class="choose-line"></span>
					</div> -->
					<!-- <div class="attractions" :class="{'choose': showContent == 2,}" @click="showContentChange(2)">
						途经景点<span class="choose-line"></span>
					</div> -->
				</div>
				<div class="content content-stations" v-show="showContent == 1">
					<div class="item station-item" v-for="item in routeInf.stations" :key="item.stationId" @click="jumpToStation(item.stationId)">
						<div class="no">{{item.orderNumber}}.</div>
						<div class="name">{{item.stationName}}</div>
					</div>
				</div>
				<!-- <div class="content content-attractions" v-show="showContent == 2">
					<div class="item attraction-item" v-for="i in stations" :key="i">
						<div class="no">{{i+1}}.</div>
						<div class="name">景点名称{{i+1}}</div>
					</div>
				</div> -->
			</div>
		</div>
		<div class="footer">
			<img src="../../assets/xxt_industrial_internet/GCI_logo.png" alt="">
			广州交信投科技股份有限公司
		</div>
	</div>
</template>

<script>
	import { postByRoute } from '@/api/api_ii.js'
	export default {
		components: {},
		props: {},
		data() {
			return {
				lines: [0, 1, 2, 3, 4, 5, 6, 7],
				stations: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
				showContent: 1,
				routeInf: {
					analysis: "",
					buses: [],
					downFirstTime: "",
					downLastTime: "",
					fromStationName: "",
					id: "",
					lastStationName: "",
					organName: "",
					routeId: "",
					routeName: "",
					stations: [],
					ticket: "",
					upFirstTime: "",
					upLastTime: "",
				}
			};
		},
		watch: {},
		computed: {},
		created() {},
		mounted() {
			let query = this.$route.query;
			let id = query.routeId;
			this.getRouteInf(id)
		},
		methods: {
			getRouteInf(id) {
				let sendData = {
					routeId: id ? id : '',
				}
				postByRoute(sendData).then((res) => {
					console.log('线路信息', res);
					this.routeInf = res
		 			document.title = this.routeInf.routeName
				})
			},
			showContentChange(type) {
				this.showContent = type
			},
			jumpToBus(id) {
				this.$router.push({path: `/ii_bus`, query: {busId: id}});
			},
			jumpToStation(id) {
				this.$router.push({path: `/ii_station`, query: {stationId: id}});
			},
			jumpToRouter() {
				this.$router.push({path: `/ii_route`, query: {routeId: 1}});
			}
		},
	};
</script>

<style lang='less' scoped>
	.container {
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

			.route-from-to {
				width: 100%;
				background: #fff;
				padding: 12px 12px 6px;
				border-radius: 16px;

				>div {
					margin-bottom: 6px;
					display: flex;
					justify-content: space-between;
					align-items: center;

					img {
						height: 16px;
						border-radius: 6px;
						display: block;
					}

					.from,
					.to {
						width: 40%;
						padding: 4px;
						background: linear-gradient(to right, #506BFF, #3379FE);
						border-radius: 4px;
						color: #fff;
						font-size: 18px;
						text-align: center;
						white-space: nowrap;
					}

					.l {
						text-align-last: left;
					}

					.r {
						text-align: right;
					}
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

			.route-station {
				padding: 12px 0;
				background: #fff;
				border-radius: 6px;
				box-shadow: 0 0 5px rgba(165, 178, 255, 0.53);

				>.title {
					padding: 0 12px;
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

					// >div {
					// 	width: 50%;
					// 	color: #333333;
					// 	font-size: 15px;
					// 	text-align: center;

					// 	span {
					// 		width: 48px;
					// 		height: 4px;
					// 		margin: 6px auto 0;
					// 		border-radius: 2px;
					// 		display: block;
					// 	}
					// }

					.choose {
						color: #506AFF;

						span {
							background: linear-gradient(to right, #506BFF, #3379FE);
						}
					}
				}

				.content {
					color: #333333;
					font-size: 14px;

					.item {
						padding: 4px 16px;
						display: flex;
						font-size: 14px;

						.no {
							width: 24px;
							margin-right: 16px;
							white-space: nowrap;
						}

						.name {}
					}

					.item:nth-child(2n) {
						background: #F0F2F8;
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