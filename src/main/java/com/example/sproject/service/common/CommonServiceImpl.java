package com.example.sproject.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sproject.dao.common.CommonGroupDao;
import com.example.sproject.model.common.CommonGroup;

@Service
public class CommonServiceImpl implements CommonService {
	@Autowired
	CommonGroupDao commonGroupDao;
	
	@Override
	//특정 tb_code에 해당하는 CommonGroup들을 순서에 맞게 리스트 형태로 저장하는 메소드
	public List<CommonGroup> listCommonGroup(String tb_code) {
		List<CommonGroup> CommonGroupList = commonGroupDao.selectList(tb_code);
		return CommonGroupList;
	}

	@Override
	public int addCommonGroup(CommonGroup commonGroup) {
		//parent_cg_num 통해서 나머지 parent값 찾기
		CommonGroup parentCommonGroup = commonGroupDao.selectOneParentCommonGroup(commonGroup);
		commonGroup.setParent_cg_ref(parentCommonGroup.getCg_ref());
		commonGroup.setParent_cg_order(parentCommonGroup.getCg_order());
		commonGroup.setParent_cg_depth(parentCommonGroup.getCg_depth());
		
		//cg_ref, cg_depth 값 설정
		commonGroup.setCg_ref(commonGroup.getParent_cg_ref());
		commonGroup.setCg_depth(commonGroup.getParent_cg_depth() + 1);
		
		//삽입될 cg_num 찾기
		String tb_code = commonGroup.getTb_code();
		int cg_num = commonGroupDao.selectOneMaxCg_num(tb_code) + 1;
		commonGroup.setCg_num(cg_num);
		
		//삽입될 cg_order 값 찾기
		int insertedCg_order = commonGroupDao.selectOneInsertedCg_order(commonGroup);
		if (insertedCg_order < 0) { //해당 ref에서 사이에 삽입되는 것이 아닌 제일 뒤에 삽입되는 경우
			insertedCg_order = commonGroupDao.selectOneMaxCg_order(commonGroup) + 1;
		}
		commonGroup.setCg_order(insertedCg_order);
		
		//기존 CommonGroup들 order 한칸씩 뒤로밀어버리기
		commonGroupDao.pushCg_order(commonGroup);
		
		//CommonGroup 삽입하기
		return commonGroupDao.insertCommonGroup(commonGroup);
	}
	
}
