package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.util.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.enums.StatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jxc
 * @since 2022-09-27
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {

        String phone = loginVo.getPhone();  //得到手机号
        String code = loginVo.getCode();    //得到验证码

        //1.校验参数
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "手机号或验证码为空");
        }

        //2. 校验校验验证码
        String redisCode = (String) redisTemplate.opsForValue().get(phone);

        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)) {
            throw new YyghException(20001, "验证码有误");
        }

        //看是否携带openid
        String openid = loginVo.getOpenid();
        //3.查验手机号是否被注册
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (StringUtils.isEmpty(openid)) {   //没有携带openid，说明是用手机号登录

            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                baseMapper.insert(userInfo);
                userInfo.setStatus(1);
            }
        } else { //说明是微信扫码登录
            QueryWrapper<UserInfo> queryWrapperWechat = new QueryWrapper<>();
            queryWrapperWechat.eq("openid", openid);
            UserInfo userInfoWechat = baseMapper.selectOne(queryWrapperWechat);
            if (userInfo == null) {  //根据openid查找用户信息，说明是第一次绑定手机号
                userInfoWechat.setPhone(phone);
                userInfo = userInfoWechat;
            } else { //说明之前用手机登录过，那就微信和手机号进行绑定
                userInfo.setOpenid(userInfoWechat.getOpenid());
                userInfo.setNickName(userInfoWechat.getNickName());
                //重新保存
                baseMapper.updateById(userInfo);
                //删除原来微信的那条记录
                baseMapper.deleteById(userInfoWechat.getId());
            }
        }


        //4.验证状态
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001, "账号被锁定");
        }
        //5.返回页面显示名称
        Map<String, Object> resultMap = this.getResultMap(userInfo);

        return resultMap;
    }

    @Override
    public String loginWechat(String code, String state, String appid, String appsecret) throws Exception {
        //        https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://api.weixin.qq.com/sns/oauth2/access_token?")
                .append("appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String url = String.format(stringBuilder.toString(), appid, appsecret, code);
        String accessTokenInfo = HttpClientUtils.get(url);
        JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
        //访问微信服务器的凭证
        String accessToken = jsonObject.getString("access_token");
        //扫码确认登录的用户在微信服务器的唯一标识
        String openid = jsonObject.getString("openid");

        //拿着唯一标识openid，去数据库当中查，这个用户是不是第一次扫码
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        UserInfo selectUserInfo = baseMapper.selectOne(queryWrapper);

        //如果查出来为空,去回调微信服务器的接口，查出来这个人的微信昵称
        if (selectUserInfo == null) {
            //清空一下stringBuilder的缓存
            stringBuilder.setLength(0);
            //拿着openid  和  access_token请求微信地址
            stringBuilder.append("https://api.weixin.qq.com/sns/userinfo")
                    .append("?access_token=%s")
                    .append("&openid=%s");
            String userInfoUrl = String.format(stringBuilder.toString(), accessToken, openid);
            String resultInfo = HttpClientUtils.get(userInfoUrl);
            System.out.println("resultInfo:" + resultInfo);
            JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
            //解析用户信息
            //用户昵称
            String nickname = resultUserInfoJson.getString("nickname");
            //用户头像
//            String headimgurl = resultUserInfoJson.getString("headimgurl");

            //保存在数据库中
            selectUserInfo = new UserInfo();
            selectUserInfo.setNickName(nickname);
            selectUserInfo.setOpenid(openid);
            baseMapper.insert(selectUserInfo);
            selectUserInfo.setStatus(1);
        }
        //验证状态
        if (selectUserInfo.getStatus() == 0) {
            throw new YyghException(20001, "账号被锁定");
        }

        //返回页面显示名称
        Map<String, Object> resultMap = this.getResultMap(selectUserInfo);

        //检查这个用户的手机号是否绑定，如果绑定了，就返回一个字段，如果未绑定，强制绑定

        /*
          //判断userInfo是否有手机号，如果手机号为空，返回openid
        //如果手机号不为空，返回openid值是空字符串
        //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号

        这里跟他逻辑写成一样
         */
        if (StringUtils.hasText(selectUserInfo.getPhone())) {
            resultMap.put("openid", "");
        } else {
            resultMap.put("openid", openid);
        }
        return "redirect:http://localhost:3000/weixin/callback?token=" + resultMap.get("token") + "&openid=" + resultMap.get("openid") + "&name=" + URLEncoder.encode((String) resultMap.get("name"), "utf-8");
    }

    @Override
    public UserInfo getUserInfoDetail(String token) {
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = baseMapper.selectById(userId);
        Map<String, Object> param = userInfo.getParam();
        param.put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        userInfo.setParam(param);
        return userInfo;
    }

    @Override
    public IPage<UserInfo> selectPage(Long page, Long limit, UserInfoQueryVo userInfoQueryVo) {

        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            queryWrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd);
        }

        Page<UserInfo> p = new Page<>(page, limit);

        IPage<UserInfo> userInfoPage = baseMapper.selectPage(p, queryWrapper);

        userInfoPage.getRecords().forEach(item -> this.packageUserInfo(item));
        return userInfoPage;
    }

    @Override
    public void lock(Long userId, Integer status) {
        //校验status
        if (status != 0 && status != 1) {
            return;
        }
//        status 0：锁定 1：正常
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null) {
            return;
        }
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public Map<String, Object> showUserInfoAndPatient(Long userId) {
        //1.查询用户的详细信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //2.查询用户添加的就诊人信息
        List<Patient> patientList = patientService.findAllByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", userInfo);
        map.put("patientList", patientList);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus != -1 && authStatus != 2) {
            return;
        }
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null) {
            throw new YyghException(20001, "没有此用户...");
        }

        //更改用户状态
        userInfo.setAuthStatus(authStatus);
        baseMapper.updateById(userInfo);
    }

    private Map<String, Object> getResultMap(UserInfo userInfo) {
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getNickName();
        if (!StringUtils.hasText(name)) {
            name = userInfo.getName();
        }
        if (!StringUtils.hasText(name)) {
            name = userInfo.getNickName();
        }
        if (!StringUtils.hasText(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //添加token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    //编号变成对应值封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
//        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        String statusString = userInfo.getStatus().intValue() == 0 ? StatusEnum.LOCK.getStatusString() : StatusEnum.NORMAL.getStatusString();
        userInfo.getParam().put("statusString", statusString);
        return userInfo;
    }
}
