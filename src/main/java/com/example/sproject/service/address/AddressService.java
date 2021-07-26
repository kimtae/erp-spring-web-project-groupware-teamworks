package com.example.sproject.service.address;

import java.util.List;

import com.example.sproject.model.address.AddressGroup;
import com.example.sproject.model.login.Member;

public interface AddressService {

	int total();

	List<Member> listMember(Member member);

	List<AddressGroup> listAddressGroup(String m_id);

	List<Member> listPersonalGroup(Member member);

	int totalPersonal(int adg_num);

}