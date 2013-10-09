package com.htong.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.htong.alg.LvBo;
import com.htong.alg.MyLvBo;
import com.htong.dao.SQLDataDao;
import com.htong.domain.CalcResult;
import com.htong.domain.SQLData;
import com.htong.domain.SQLWell;
import com.htong.gzzd.GTDataComputerProcess;
import com.htong.util.FileConstants;

public class GTLYService {
	private static final Logger log = Logger.getLogger(GTLYService.class);

	private Date startDate;
	private Date endDate;
	private int timeLong = 24;
	private float loss = 0;
	private float trueValue;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");

	private void loadConfig(String wellNum, Date date) {
		Properties properties = new Properties();
		try {
			File path = new File(FileConstants.CONFIG_PATH);
			if (!path.exists()) {
				path.mkdir();
			}
			File file = new File(FileConstants.COMMON_CONFIT_PATH);
			if (!file.exists()) {
				file.createNewFile();
				return;
			}
			properties.load(new InputStreamReader(new FileInputStream(
					FileConstants.COMMON_CONFIT_PATH), "UTF-8"));
			String key = wellNum + "_" + sdf.format(date);
			String value = properties.getProperty(key, "00:00-23:59");
//			log.debug(key);
//			log.debug(value);
//
//			int start = Integer.valueOf(value.split("-")[0]);
//			int end = Integer.valueOf(value.split("-")[1]);
			
			int start_hour = Integer.valueOf(value.split("-")[0].split(":")[0]);
			int start_minute = Integer.valueOf(value.split("-")[0].split(":")[1]);
			
			int end_hour = Integer.valueOf(value.split("-")[1].split(":")[0]);
			int end_minute = Integer.valueOf(value.split("-")[1].split(":")[1]);

			Calendar startC = Calendar.getInstance();
			startC.setTime(date);
			startC.set(Calendar.HOUR_OF_DAY, start_hour);
			startC.set(Calendar.MINUTE, start_minute);
			startC.set(Calendar.SECOND, 0);
			
				Calendar endC = Calendar.getInstance();
				endC.setTime(date);
				endC.set(Calendar.HOUR_OF_DAY, end_hour);
				endC.set(Calendar.MINUTE, end_minute);
				endC.set(Calendar.SECOND, 59);

				startDate = startC.getTime();
				endDate = endC.getTime();
				log.debug(sdfAll.format(startDate));
				log.debug(sdfAll.format(endDate));
				
				timeLong = end_hour-start_hour;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadLoss(String wellNum) {
		Properties properties = new Properties();
		try {
			File path = new File(FileConstants.CONFIG_PATH);
			if (!path.exists()) {
				path.mkdir();
			}
			File file = new File(FileConstants.LOSS_PATH);
			if (!file.exists()) {
				file.createNewFile();
				return;
			}
			properties.load(new InputStreamReader(new FileInputStream(
					FileConstants.LOSS_PATH), "UTF-8"));
			this.loss = Float.valueOf(properties.getProperty(wellNum, "0").split(",")[0]);
			this.trueValue = Float.valueOf(properties.getProperty(wellNum, "2").split(",")[1]);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CalcResult getDayResult(String wellNum, Date date) {
		CalcResult cr = new CalcResult();

		loadConfig(wellNum, date);// 获取时间段
		loadLoss(wellNum);

		SQLWell sqlWell = SQLWell.getWellsMap().get(wellNum);
		List<SQLData> sqlDataList = new SQLDataDao().getDatas(wellNum,
				sdfAll.format(startDate), sdfAll.format(endDate));
		log.debug(sdfAll.format(startDate) + " " + sdfAll.format(endDate));
		
		cr.setWellNum(wellNum);
		cr.setTime(sdf.format(date));
		cr.setUnit("吨");
		cr.setNum("0");
		cr.setValue("");
		cr.setValueDun("");
		cr.setAreaValue("0");
		
		cr.setValueOld("");
		cr.setTimeLong(sdfHour.format(startDate) + "--" + sdfHour.format(endDate));
		

		if (sqlDataList != null && !sqlDataList.isEmpty()) {
			List<Float> resultList = new ArrayList<Float>();
			List<Float> chongciList = new ArrayList<Float>();
			List<Float> areaResultList = new ArrayList<Float>();
			for (SQLData data : sqlDataList) {
				String[] weiyiStr = data.getWeiyi().split(";");
				String[] zaiheStr = data.getZaihe().split(";");
				
				float[] weiyi = new float[weiyiStr.length];
				float[] zaihe = new float[zaiheStr.length];
				for(int i = 0;i<weiyiStr.length;i++) {
					weiyi[i] = Float.valueOf(weiyiStr[i]);
					zaihe[i] = Float.valueOf(zaiheStr[i]);
				}

				if (zaihe[50] < 0.5 && zaihe[51] < 0.5 && zaihe[52] < 0.5) {
					log.debug("数据均为0");
					continue;// 数据均为0
				}
				// 加入滤波算法
				MyLvBo.myLvBo(weiyi, zaihe);

				GTDataComputerProcess sp = new GTDataComputerProcess();
				Float f=0f;//立方米
				try {
					Map<String, Object> map = sp.calcSGTData(weiyi, zaihe, data.getChongci(), 0, sqlWell.getBengjing(),
							sqlWell.getMidu(), sqlWell.getHanshui());
					
					f = (Float) map.get("liquidProduct");
					log.debug("原始产液量：" + f);
					//有效冲程补偿
					Float yxcc = (Float)map.get("youxiaochongcheng");
					log.debug("有效冲程：" + yxcc);
					Float zsp = (Float) map.get("AX");
					Float chongcheng = (Float)map.get("chongcheng");
//					if(yxcc < chongcheng*3/4) {
//						float addChongCheng = zsp * (chongcheng-zsp-yxcc)/(chongcheng-zsp);
//						f += (addChongCheng/yxcc)*f;
//						log.debug("附加有效冲程：" + addChongCheng);
//					}
					Float areaResult = (Float)map.get("areaProduct");
					areaResultList.add(areaResult);
				} catch (Exception e) {
					f=0f;
					e.printStackTrace();
				}
				resultList.add(f);
				chongciList.add(data.getChongci());
				
			}
			log.debug("功图个数："+resultList.size());
			
			float zzf = LvBo.zhongzhiLB(resultList);
			log.debug("原始产液量滤波结果：" + zzf);
			
			float chongci = LvBo.zhongzhiLB(chongciList);
			log.debug("平均冲次为:" + chongci);
			
			float areaCYL = LvBo.zhongzhiLB(areaResultList);
			log.debug("平均面积产液量：" + areaCYL);
			
			float cylOld = zzf * 24;
			float cylOldDun =  cylOld*sqlWell.getHanshui()/100 + sqlWell.getMidu()*cylOld*(100-sqlWell.getHanshui())/100;	//原始产液量（吨）
			
			float oldDun = new BigDecimal(cylOldDun).setScale(3,
					BigDecimal.ROUND_HALF_UP).floatValue();
			cr.setValueOld(String.valueOf(oldDun));	//原始值 吨
			
//			float cyl = zzf * 24  - loss*(chongci/trueValue);
			float cyl = zzf * 24;
			
			cr.setNum(String.valueOf(sqlDataList.size()));
			float m3 = cyl<0?0:cyl;
			float newLiquidProduct = new BigDecimal(m3).setScale(3,
					BigDecimal.ROUND_HALF_UP).floatValue();
			
			cr.setValue(String.valueOf(newLiquidProduct));//m³
			
			
			float dun = m3*sqlWell.getHanshui()/100 + sqlWell.getMidu()*m3*(100-sqlWell.getHanshui())/100;
			
			float product = new BigDecimal(dun).setScale(3,
					BigDecimal.ROUND_HALF_UP).floatValue();
			cr.setValueDun(String.valueOf(product));//吨
			cr.setAreaValue(String.valueOf(areaCYL*24));
		}
		return cr;
	}

	public List<CalcResult> getMonthResult(String wellNum, Date date) {
		List<CalcResult> calcList = new ArrayList<CalcResult>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 1; i <= days; i++) {
			Date myDate = calendar.getTime();
			try {
				String time = calendar.get(Calendar.YEAR)
						+ "-"
						+ calendar.get(Calendar.MONTH)
						+ 1
						+ "-"
						+ (i < 10 ? "0" + String.valueOf(i) : String.valueOf(i))
						+ " " + "00:00:00";
				myDate = sdfAll.parse(time);
				log.debug(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			CalcResult calc = getDayResult(wellNum, myDate);
			calcList.add(calc);

		}

		return calcList;
	}

}
