package com.sg.mobile.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sg.mobile.dao.MobileUserLocationDao;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.entity.MobileUserLocation;
import com.sg.mobile.entity.MobileUserSosLocation;
import com.sg.mobile.util.BaiDuServiceUtil;
import com.sg.mobile.util.HaoServiceUtil;
import com.sg.service.BaseEntityManager;
import com.sg.util.DateUtil;
import com.sg.util.UrlCodeUtil;
import com.sg.weixin.entity.WeiXinInMobile;
import com.sg.weixin.entity.WeiXinUser;
import com.sg.weixin.service.WeiXinInMobileService;
import com.sg.weixin.service.WeiXinUserService;

@Service
public class MobileUserLocationService implements BaseEntityManager<MobileUserLocation> {
	@Resource
	private MobileUserLocationDao mobileUserLocationDao;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private MobileUserSosLocationService mobileUserSosLocationService;
	
	@Autowired
	private WeiXinUserService weiXinUserService;
	
	@Autowired
	private WeiXinInMobileService weiXinInMobileService;
	
	@Override
	public void create(MobileUserLocation entity) {
		// TODO Auto-generated method stub
		mobileUserLocationDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileUserLocationDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserLocation> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileUserLocationDao.find(params, pageParams);
	}

	@Override
	public MobileUserLocation get(String id) {
		// TODO Auto-generated method stub
		return mobileUserLocationDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserLocation> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileUserLocationDao.findForUnPage(params);
	}

	@Override
	public void update(MobileUserLocation entity) {
		// TODO Auto-generated method stub
		mobileUserLocationDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileUserLocationDao.getTotalCount(params);
	}
	
	public String createMobileUserLocationData(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("userName")){
			userName = jsonObject.opt("userName").toString();
		}
		
		String longitude = "";
		if(jsonObject.containsKey("longitude")){
			longitude = jsonObject.opt("longitude").toString();
		}
		
		String latitude = "";
		if(jsonObject.containsKey("latitude")){
			latitude = jsonObject.opt("latitude").toString();
		}
		
		String address = "";
		if(jsonObject.containsKey("address")){
			if(jsonObject.opt("address") != null && !jsonObject.opt("address").toString().equals("")){
				address = UrlCodeUtil.urlDeCode(jsonObject.opt("address").toString());
			}
		}
		
		if(userName != null && !userName.equals("") && longitude != null && !longitude.equals("") && latitude != null && !latitude.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				
				/*String baiDuLatitude = "";
				String baiDuLongitude = "";
				String baiDuAddress = "";
				
				try {
					
					String strBaiDuLocationDoc = getBaiDuAddressByGps(latitude, longitude);
					
					System.out.println(strBaiDuLocationDoc);
					
					Document baiDuLocationDoc = DocumentHelper.parseText(strBaiDuLocationDoc);
					
					Element rootElement = baiDuLocationDoc.getRootElement();
					Iterator rootIter = rootElement.elementIterator();
					
					while (rootIter.hasNext()) {
						Element resultElement = (Element)rootElement.elements("result").get(0);
						
						Element locationElement = (Element)resultElement.elements("location").get(0);
						
						Element latElement = (Element)locationElement.elements("lat").get(0);
						baiDuLatitude = latElement.getText();
						
						Element lngElement = (Element)locationElement.elements("lng").get(0);
						baiDuLongitude = lngElement.getText();
						
						Element formattedAddressElement = (Element)resultElement.elements("formatted_address").get(0);
						baiDuAddress = formattedAddressElement.getText();
						
						break;
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				MobileUserLocation mobileUserLocation = new MobileUserLocation();
				String id = UUID.randomUUID().toString();
				mobileUserLocation.setId(id);
				mobileUserLocation.setOwnerUri(ownerUri);
				mobileUserLocation.setLongitude(longitude);
				mobileUserLocation.setLatitude(latitude);
				mobileUserLocation.setAddress(address);
				mobileUserLocation.setCreator(ownerUri);
				mobileUserLocation.setStatus("Y");
				
				create(mobileUserLocation);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", "200");
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "没有相关用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String createMobileUserLocationJsonByMin(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("user")){
			userName = jsonObject.opt("user").toString();
		}
		
		String type = "";
		if(jsonObject.containsKey("type")){
			type = jsonObject.opt("type").toString();
		}
		
		String isSos = "";
		if(jsonObject.containsKey("isSos")){
			isSos = jsonObject.opt("isSos").toString();
		}
		
		if(userName != null && !userName.equals("") && type != null && !type.equals("")){
			
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				String id = UUID.randomUUID().toString();
				
				if(type.equals("gps")){
					String latitude = "";
					if(jsonObject.containsKey("latitude")){
						latitude = jsonObject.opt("latitude").toString();
					}
					
					String longitude = "";
					if(jsonObject.containsKey("longitude")){
						longitude = jsonObject.opt("longitude").toString();
					}
					
					json = processGpsLocaiton(latitude, longitude, isSos, id, ownerUri);
					
					return json;
				}else if(type.equals("mix")) {
					String celltowers = "";
					if(jsonObject.containsKey("celltowers")){
						celltowers = jsonObject.opt("celltowers").toString();
					}
					
					String wifilist = "";
					if(jsonObject.containsKey("wifilist")){
						wifilist = jsonObject.opt("wifilist").toString();
					}
					
					JSONObject requestData = new JSONObject();
					
					boolean celltowersIsPass = false;
					if(celltowers != null && !celltowers.equals("")){
						JSONArray celltowersJsonArray = JSONArray.fromObject(celltowers);
						
						if(celltowersJsonArray != null && celltowersJsonArray.size() > 0){
							celltowersIsPass = true;
							
							requestData.put("celltowers", celltowers);
						}
					}
					
					boolean wifilistIsPass = false;
					if(wifilist != null && !wifilist.equals("")){
						JSONArray wifilistJsonArray = JSONArray.fromObject(wifilist);
						
						if(wifilistJsonArray != null && wifilistJsonArray.size() > 0){
							wifilistIsPass = true;
							
							requestData.put("wifilist", wifilist);
						}
					}
					
					if(celltowersIsPass || wifilistIsPass){
						String mnctype = "";
						if(jsonObject.containsKey("mnctype")){
							mnctype = jsonObject.opt("mnctype").toString();
						}
						
						if(mnctype != null && !mnctype.equals("")){
							requestData.put("mnctype", mnctype);
							
							json = processMixLocaiton(requestData.toString(), isSos, id, ownerUri);
							
							return json;
						}else {
							jsonObjectResult.put("result", "0");
							jsonObjectResult.put("message", "缺少必要的参数");
						}
					}else {
						jsonObjectResult.put("result", "0");
						jsonObjectResult.put("message", "缺少必要的参数");
					}
				}else {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", "未定义类型");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "非法用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String processGpsLocaiton(String latitude, String longitude, String isSos, String id, String ownerUri){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String baiDuLatitude = "";
		String baiDuLongitude = "";
		String baiDuAddress = "";
		
		try {
			
			String strBaiDuLocationDoc = BaiDuServiceUtil.getBaiDuAddressByGps(latitude, longitude);
			
			Document baiDuLocationDoc = DocumentHelper.parseText(strBaiDuLocationDoc);
			
			Element rootElement = baiDuLocationDoc.getRootElement();
			Iterator rootIter = rootElement.elementIterator();
			
			while (rootIter.hasNext()) {
				Element resultElement = (Element)rootElement.elements("result").get(0);
				
				Element locationElement = (Element)resultElement.elements("location").get(0);
				
				Element latElement = (Element)locationElement.elements("lat").get(0);
				baiDuLatitude = latElement.getText();
				
				Element lngElement = (Element)locationElement.elements("lng").get(0);
				baiDuLongitude = lngElement.getText();
				
				Element formattedAddressElement = (Element)resultElement.elements("formatted_address").get(0);
				baiDuAddress = formattedAddressElement.getText();
				
				break;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(baiDuLatitude != null && !baiDuLatitude.equals("") && baiDuLongitude != null && !baiDuLongitude.equals("") && baiDuAddress != null && !baiDuAddress.equals("")){
			MobileUserLocation mobileUserLocation = new MobileUserLocation();
			mobileUserLocation.setId(id);
			mobileUserLocation.setOwnerUri(ownerUri);
			mobileUserLocation.setLongitude(baiDuLongitude);
			mobileUserLocation.setLatitude(baiDuLatitude);
			mobileUserLocation.setAddress(baiDuAddress);
			mobileUserLocation.setCreator(ownerUri);
			mobileUserLocation.setStatus("Y");
			
			create(mobileUserLocation);
			
			if(isSos != null && !isSos.equals("") && isSos.equals("Y")){
				MobileUserSosLocation mobileUserSosLocation = new MobileUserSosLocation();
				mobileUserSosLocation.setId(id);
				mobileUserSosLocation.setOwnerUri(ownerUri);
				mobileUserSosLocation.setLongitude(baiDuLongitude);
				mobileUserSosLocation.setLatitude(baiDuLatitude);
				mobileUserSosLocation.setAddress(baiDuAddress);
				mobileUserSosLocation.setCreator(ownerUri);
				mobileUserSosLocation.setStatus("Y");
				
				mobileUserSosLocationService.create(mobileUserSosLocation);
			}
			
			JSONObject jsonData = new JSONObject();
			jsonData.put("longitude", longitude);
			jsonData.put("latitude", latitude);
			jsonData.put("address", baiDuAddress);
			
			jsonObjectResult.put("result", "1");
			jsonObjectResult.put("data", jsonData.toString());
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "百度逆地址接口调用异常.");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String processMixLocaiton(String requestData, String isSos, String id, String ownerUri){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String haoLatitude = "";
		String haoLongitude = "";
		String haoAddress = "";
		
		try {
			/*返回坐标类型默认值 0(google坐标),1(百度坐标),2(gps坐标)*/
			String type = "2";
			
			String key = "bad7c699095646faad48439715b8fe4d";
			
			String strHaoLocationJson = HaoServiceUtil.getLocationByMix(requestData, type, key);
			
			JSONObject jsonObject = JSONObject.fromObject(strHaoLocationJson);
			
			String errCode = "";
			if(jsonObject.containsKey("ErrCode")){
				errCode = jsonObject.opt("ErrCode").toString();
			}
			
			if(errCode != null && !errCode.equals("") && errCode.equals("0")){
				String location = "";
				if(jsonObject.containsKey("location")){
					location = jsonObject.opt("location").toString();
				}
				
				if(location != null && !location.equals("")){
					JSONObject jsonObjectLocation = JSONObject.fromObject(location);
					
					if(jsonObjectLocation.containsKey("latitude")){
						haoLatitude = jsonObjectLocation.opt("latitude").toString();
					}
					
					if(jsonObjectLocation.containsKey("longitude")){
						haoLongitude = jsonObjectLocation.opt("longitude").toString();
					}
					
					if(jsonObjectLocation.containsKey("addressDescription")){
						haoAddress = jsonObjectLocation.opt("addressDescription").toString();
					}
				}else {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", strHaoLocationJson);
					
					return json;
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", strHaoLocationJson);
				
				return json;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(haoLatitude != null && !haoLatitude.equals("") && haoLongitude != null && !haoLongitude.equals("") && haoAddress != null && !haoAddress.equals("")){
			MobileUserLocation mobileUserLocation = new MobileUserLocation();
			mobileUserLocation.setId(id);
			mobileUserLocation.setOwnerUri(ownerUri);
			mobileUserLocation.setLongitude(haoLongitude);
			mobileUserLocation.setLatitude(haoLatitude);
			mobileUserLocation.setAddress(haoAddress);
			mobileUserLocation.setCreator(ownerUri);
			mobileUserLocation.setStatus("Y");
			
			create(mobileUserLocation);
			
			if(isSos != null && !isSos.equals("") && isSos.equals("Y")){
				MobileUserSosLocation mobileUserSosLocation = new MobileUserSosLocation();
				mobileUserSosLocation.setId(id);
				mobileUserSosLocation.setOwnerUri(ownerUri);
				mobileUserSosLocation.setLongitude(haoLongitude);
				mobileUserSosLocation.setLatitude(haoLatitude);
				mobileUserSosLocation.setAddress(haoAddress);
				mobileUserSosLocation.setCreator(ownerUri);
				mobileUserSosLocation.setStatus("Y");
				
				mobileUserSosLocationService.create(mobileUserSosLocation);
			}
			
			JSONObject jsonData = new JSONObject();
			jsonData.put("longitude", haoLongitude);
			jsonData.put("latitude", haoLatitude);
			jsonData.put("address", haoAddress);
			
			jsonObjectResult.put("result", "1");
			jsonObjectResult.put("data", jsonData.toString());
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "haoService接口调用异常.");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String getMobileUserLocationByOpenIdJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String openId = "";
		if(jsonObject.containsKey("openId")){
			openId = jsonObject.opt("openId").toString();
		}
		
		String startTime = "";
		if(jsonObject.containsKey("startTime")){
			startTime = jsonObject.opt("startTime").toString();
		}
		
		String endTime = "";
		if(jsonObject.containsKey("endTime")){
			endTime = jsonObject.opt("endTime").toString();
		}
		
		if(openId != null && !openId.equals("") && startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")){
			WeiXinUser weiXinUser = weiXinUserService.getWeiXinUserByOpenId(openId);
			
			if(weiXinUser != null){
				String weiXinOwnerUri = weiXinUser.getOwnerUri();
				
				Map<String, Object> weiXinInMobileParams = new HashMap<String, Object>();
				weiXinInMobileParams.put("weiXinOwnerUri", weiXinOwnerUri);
				weiXinInMobileParams.put("status", "Y");
				List<WeiXinInMobile> weiXinInMobiles = weiXinInMobileService.findForUnPage(weiXinInMobileParams);
				
				if(weiXinInMobiles != null && weiXinInMobiles.size() > 0){
					JSONArray jsonArrayMobileUserLocation = new JSONArray();
					
					for (WeiXinInMobile weiXinInMobile : weiXinInMobiles) {
						String mobileOwnerUri = weiXinInMobile.getMobileOwnerUri();
						
						Map<String, Object> mobileUserLocationParams = new HashMap<String, Object>();
						mobileUserLocationParams.put("ownerUri", mobileOwnerUri);
						mobileUserLocationParams.put("startTime", startTime);
						mobileUserLocationParams.put("endTime", endTime);
						
						List<MobileUserLocation> mobileUserLocations = findForUnPage(mobileUserLocationParams);
						
						if(mobileUserLocations != null && mobileUserLocations.size() > 0){
							for (MobileUserLocation mobileUserLocation : mobileUserLocations) {
								JSONObject jsonObjectMobileUserLocation = new JSONObject();
								
								jsonObjectMobileUserLocation.put("ownerUri", mobileUserLocation.getOwnerUri());
								jsonObjectMobileUserLocation.put("longitude", mobileUserLocation.getLongitude());
								jsonObjectMobileUserLocation.put("latitude", mobileUserLocation.getLatitude());
								jsonObjectMobileUserLocation.put("address", mobileUserLocation.getAddress() == null ? "" : mobileUserLocation.getAddress());
								
								String strDate = DateUtil.getDate(mobileUserLocation.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
								
								jsonObjectMobileUserLocation.put("createTime", strDate);
								
								jsonArrayMobileUserLocation.add(jsonObjectMobileUserLocation);
							}
						}
					}
					
					if(jsonArrayMobileUserLocation != null && jsonArrayMobileUserLocation.size() > 0){
						jsonObjectResult.put("result", "1");
						jsonObjectResult.put("data", jsonArrayMobileUserLocation.toString());
					}else {
						jsonObjectResult.put("result", "1");
						jsonObjectResult.put("data", "");
					}
				}else {
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", "");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "非法用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
