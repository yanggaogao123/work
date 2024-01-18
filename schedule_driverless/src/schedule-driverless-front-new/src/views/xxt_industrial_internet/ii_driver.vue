<template>
	<div class="container">
		<div class="top">
			<div class="code">标识码：{{empInf.id}}</div>
			<div class="show-num">本标识码已解析<span>{{empInf.analysis}}</span>次</div>
		</div>
		<div class="main">
			<div class="driver-inf">
				<div class="driver-code">
					资格证号：{{empInf.qualification}}
				</div>
				<div class="score">
					<div class="title">星级：</div>
					<div class="score-list">
						<img src="../../assets/xxt_industrial_internet/star2.png" alt="" v-for="i in Number(empInf.star)" :key="'star' + i">
						<img src="../../assets/xxt_industrial_internet/star.png" alt="" v-for="i in 5 - Number(empInf.star)" :key="'hollow' + i">
					</div>
				</div>
			</div>
			<div class="inf" v-show="0">
				<div class="title">
					<div class="line"></div>
					<div class="txt">星级说明</div>
				</div>
				<div class="score-content">
					<div class="score">一星：说明文字说明文字说明文字说明文字说明文字说明文字说明文字说明文字</div>
					<div class="score">二星：说明文字说明文字说明文字说明文字说明文字说明文字说明文字</div>
					<div class="score">三星：说明文字说明文字说明文字说明文字说明文字说明文字</div>
					<div class="score">四星：说明文字说明文字说明文字说明文字说明文字说明文字说明文字</div>
					<!-- <div class="score">五星：说明文字说明文字说明文字说明文字说明文字说明文字说明文字</div> -->
				</div>
			</div>
			<div class="inf" v-show="0">
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
	import { postByEmp } from '@/api/api_ii.js'
	export default {
		components: {},
		props: {},
		data() {
			return {
				empInf: {},
			};
		},
		watch: {},
		computed: {},
		created() {},
		mounted() {
			let query = this.$route.query;
			let id = query.empId;
			this.getEmpInf(id)
		},
		methods: {
			getEmpInf(id) {
				let sendData = {
					empId: id ? id : ''
				}
				postByEmp(sendData).then((res) => {
					console.log('站点信息', res);
					this.empInf = res
				})
			},
			showContentChange(type) {
				this.showContent = type
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

			.driver-inf {
				width: 100%;
				background: #fff;
				padding: 20px 26px;
				border-radius: 16px;
				color: #333333;
				font-size: 18px;
				font-weight: 600;
				.score {
					margin-top: 16px;
					display: flex;
					align-items: center;
					.score-list {
						img {
							width: 20px;
							height: 20px;
							margin-right: 6px;
						}
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