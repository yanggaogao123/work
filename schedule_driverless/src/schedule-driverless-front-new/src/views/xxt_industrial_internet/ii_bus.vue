<template>
	<div class="container">
		<div class="top">
			<div class="code">标识码：{{busInf.id}}</div>
			<div class="show-num">本标识码已解析<span>{{busInf.analysis}}</span>次</div>
		</div>
		<div class="main">
			<div class="route-from-to">
				<div class="route-inf">
					<div class="l">
						<div class="route-name" @click="jumpToRouter(busInf.routeId)">{{busInf.routeName}}</div>
						<div class="bus-status">{{busInf.crowd}}</div>
					</div>
					<div class="r" @click="jumpToDriver(busInf.employeeId)"><img src="../../assets/xxt_industrial_internet/driver_logo.png" alt="">查看驾驶员信息</div>
				</div>
				<div class="from-to">
					<div class="from" v-if="stationList.d.l[0].n.length < 7" @click="jumpToStation(stationList.d.l[0].i)">{{stationList.d.l[0].n}}</div>
					<marquee class="from" v-else @click="jumpToStation(stationList.d.l[0].i)">{{stationList.d.l[0].n}}</marquee>
					<img class="arrow" src="../../assets/xxt_industrial_internet/arrow.png" />
					<div class="to" v-if="stationList.d.l[stationList.d.l.length - 1].n.length < 7" @click="jumpToStation(stationList.d.l[stationList.d.l.length - 1].i)">{{stationList.d.l[stationList.d.l.length - 1].n}}</div>
					<marquee class="to" v-else @click="jumpToStation(stationList.d.l[stationList.d.l.length - 1].i)">{{stationList.d.l[stationList.d.l.length - 1].n}}</marquee>
				</div>
			</div>
			<div class="route-station">
				<div class="title">
					<div class="line"></div>
					<div class="txt">途经站点</div>
				</div>
				<div class="next-station">
					<div class="l">下一站：</div>
					<div class="r" @click="jumpToStation(busInf.stationId)">{{busInf.stationName}}</div>
				</div>
				<div class="content content-stations" v-show="showContent == 1">
					<div class="item station-item" v-for="(item, index) in stationList.d.l" :key="item.i" @click="jumpToStation(item.i)">
						<div class="left">
							<div class="no">{{index + 1}}.</div>
							<div class="name" v-if="item.n.length < 12">{{item.n}}</div>
							<marquee class="name" v-else>{{item.n}}</marquee>
						</div>
						<div class="right">
							<!-- <span class="gray" v-show="i%2 == 0">{{'已到站'}}</span>
							<span class="blue" v-show="i%2 != 0">{{'预计11:45到站'}}</span> -->
							<span class="send" v-if="index == 0">预计发车{{stationList.d.fbt}}</span>
							<span class="gray" v-else-if="item.ti == -1">已过站</span>
							<span class="blue" v-else-if="item.ti != -1"><span style="color:orange;">{{item.ti}}分钟</span>后到达此站</span>
						</div>
					</div>
				</div>
			</div>
			<div class="inf">
				<div class="title">
					<div class="line"></div>
					<div class="txt">公司介绍</div>
				</div>
				<div class="content">
					      广州市一汽巴士有限公司前身为广州市第一公共汽车公司，是我市解放后第一家国有公交企业。2004年，经市政府批准进行整体改制，通过增资形式注入民营资本，国有和民营方各50%资本组建成为广州市一汽巴士有限公司。
					　　 公司经营公交线路189条，线路总长度达2500多公里，营运车辆2352辆，庞大的公交网络覆盖广州，年载客运量达4.64亿人次，约占广州公交客运量19％，实现“东至黄埔、南至番禺、西至芳村、北至白云”，是我市目前历史最悠久、规模最大的公交企业之一。
					　　广州市一汽巴士有限公司继承“友爱在车厢，真诚为乘客”的企业服务品牌，把“为市民提供五星级服务”作为企业永远追求的服务理念，以信息化改造传统公交，不断完善文明礼貌服务规范、安全服务工作规程和车辆清洁卫生标准，全面增强员工爱岗敬业、友爱诚信意识，主动配合政府实施“公交优先”战略，以安全、方便、舒适、快捷、环保体现交通出行“幸福指数”，以科学发展观构建和谐公交，为市民出行提供更为优质的服务，真正成为市民满意、政府信任的公交企业。
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
	import { postBySubId } from "@/api/api";
	import { postBytBus } from '@/api/api_ii.js'
	export default {
		components: {},
		props: {},
		data() {
			return {
				busInf: {
					analysis: "",
					busId: "",
					crowd: "",
					employeeId: "",
					employeeName: "",
					id: "",
					numberPlate: "",
					routeId: "",
					routeName: "",
					stationId: "",
					stationName: "",
				},
				stationList: [],
				showContent: 1,
			};
		},
		watch: {},
		computed: {},
		created() {},
		mounted() {
			let query = this.$route.query;
			let id = query.busId;
			this.getBusInf(id)
			this.getStationList(id)
		},
		methods: {
			getBusInf(id) {
				let sendData = {
					busId: id ? id : ''
				}
				postBytBus(sendData).then((res) => {
					console.log('站点信息', res);
					this.busInf = res
		 			document.title = this.busInf.numberPlate
				})
			},
			getStationList(id) {
				postBySubId({
					// subId: '767557',
					busId: id
				}).then(res => {
					if (res.retCode == 0) {
						this.stationList = res.retData;
					} else {
						this.$router.go(-1)
					}
				});
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
			jumpToRouter(id) {
				this.$router.push({path: `/ii_route`, query: {routeId: id}});
			},
			jumpToDriver(id) {
				this.$router.push({path: `/ii_driver`, query: {empId: id}});
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

				.route-inf {
					.l {
						width: 35%;
						display: flex;
						justify-content: space-between;
						align-items: center;
						.route-name {
							font-size: 20px;
							color: #506AFF;
						}
						.bus-stauts {

						}
					}

					.r {
						width: 52%;
						padding: 6px 0;
						border: 1px dashed #506AFF;
						border-radius: 4px;
						color: #506AFF;
						font-size: 16px;
						text-align: center;
						white-space: nowrap;
						img {
							height: 16px;
							margin-right: 4px;
    					vertical-align: text-bottom;
							display: inline-block;
						}
					}
				}
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
						font-size: 16px;
						text-align: center;
						white-space: nowrap;
					}

				}
			}

			.route-station {
				padding: 12px 0;
				background: #fff;
				border-radius: 6px;
				box-shadow: 0 0 5px rgba(165, 178, 255, 0.53);

				>.title {
					padding: 0 16px;
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

				.next-station {
					padding: 0 16px;
					margin-bottom: 6px;
					display: flex;
					justify-content: space-between;
					align-items: center;
					.l {
						font-size: 14px;
					}
					.r {
						height: 28px;
						line-height: 28px;
						padding: 0 14px;
						border: 1px dashed #506AFF;
						border-radius: 4px;
						color: #506AFF;
						font-size: 16px;
					}
				}

				.content {
					color: #333333;
					font-size: 14px;

					.item {
						padding: 4px 16px;
						display: flex;
						font-size: 14px;
						justify-content: space-between;
						align-items: center;
						.left {
							display: flex;
							.no {
								width: 24px;
								margin-right: 16px;
								white-space: nowrap;
							}

							.name {}
						}
						.right {
							white-space: nowrap;
							span {
								font-size: 13px;
							}
							.gray {
								color: #888888;
							}
							.blue {
								color: #506AFF;
							}
						}
					}

					.item:nth-child(2n) {
						background: #F0F2F8;
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