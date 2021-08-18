package com.example.sproject.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.sproject.configuration.WebMvcConfig;
import com.example.sproject.model.drive.DriveFileInfo;
import com.example.sproject.model.globals.GlobalsOfCg_num;
import com.example.sproject.model.globals.GlobalsOfMail;
import com.example.sproject.model.login.Member;
import com.example.sproject.model.mail.Mail;
import com.example.sproject.model.mail.MailTo;
import com.example.sproject.service.common.CommonPaging;
import com.example.sproject.service.drive.DriveService;
import com.example.sproject.service.mail.MailService;
import com.example.sproject.service.sample.EmailReader;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.mail.smtp.SMTPTransport;

@Controller
@RequestMapping("mail")
public class MailController {
	
	static final String MAIL_DOMAIN = "@teamworksgroup.shop";
	
	static final String MODE_RECEIVED = "received"; // 받은 메일함
	static final String MODE_SENT = "sent"; // 보낸 메일함
	
	@Autowired
	EmailReader emailReader;
	@Autowired
	MailService mailService;
	@Autowired
	DriveService driveService;
	
    /**
     * 받은 메일 리스트 가져오기
     * @throws MessagingException 
     * @throws ParseException 
     */
    @RequestMapping(value ="", method= {RequestMethod.GET, RequestMethod.POST})
    public String index(String mode, Integer currentPage, @AuthenticationPrincipal Member principal, Model model) throws MessagingException, ParseException {
    	// 메일 가져오기 위한 정보 세팅
    	Mail mail = new Mail();
    	mail.setM_id(principal.getM_id());

    	
    	// 모드에 따른 세팅
    	if (mode == null || mode.equals("")) {
    		mode = MODE_RECEIVED;
    	}
    	if (mode.equals(MODE_RECEIVED)) {
    		// JSP 모드 설정
    		model.addAttribute("mode", MODE_RECEIVED);
    		
        	// 메일 DB 업데이트하기
        	mailService.updateMailDB();
        	Timestamp updateDateOfDb = mailService.findUpdateDateOfDb();
        	model.addAttribute("updateDateOfDb",updateDateOfDb);
        	
        	// 메일 가져오기 위한 정보 세팅
        	mail.setMl_type(1); // 받은 메일
        	mail.setMl_is_deleted(0);		
    	} else if (mode.equals(MODE_SENT)) {
    		// JSP 모드 설정
    		model.addAttribute("mode", MODE_SENT);
    		
        	// 메일 가져오기 위한 정보 세팅
        	mail.setMl_type(2); // 보낸 메일
        	mail.setMl_is_deleted(0);    		
    	}
    	
    	
    	// 페이징 처리
    	int rowPage = 10;
    	int pageBlock = 5;    	
    	CommonPaging commonPaging = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		commonPaging = new CommonPaging(mailService.countTotalMailReceived(mail), currentPage, rowPage, pageBlock);
    	} else if (mode.equals(MODE_SENT)) {
    		commonPaging = new CommonPaging(mailService.countTotalMailSent(mail), currentPage, rowPage, pageBlock);
    	}
    	System.out.println("commonPaging: " + commonPaging);
    	model.addAttribute("commonPaging", commonPaging);
    	mail.setRn_start(commonPaging.getStart());
    	mail.setRn_end(commonPaging.getEnd());
    	
    	// 페이징 처리에 맞게 메일 가져오기
    	List<Mail> listOfMail = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		listOfMail = mailService.listMailReceived(mail); 
    	} else if (mode.equals(MODE_SENT)) {
    		listOfMail = mailService.listMailSent(mail);
    	}
    	mailService.replaceStringForHtml(listOfMail);
    	model.addAttribute("listOfMail", listOfMail);
    	
    	// 메일 수 카운트하기
    	mail.setRn_start(1);
    	mail.setRn_end(Integer.MAX_VALUE);
    	List<Mail> listOfMailForCounting = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		listOfMailForCounting = mailService.listMailReceived(mail);
    	} else if (mode.equals(MODE_SENT)) {
    		listOfMailForCounting = mailService.listMailSent(mail);
    	}
    	int numRead = 0;
    	for (Mail m : listOfMailForCounting) {
    		if (m.getMl_is_read() == 1) {
    			++numRead;
    		}
    	}
    	model.addAttribute("numRead", numRead);
    	model.addAttribute("numUnread", listOfMailForCounting.size() - numRead);   
    	
    	
    	
        return "mail/mailMain";
    }
    
    /**
     * 메일 내용 읽기
     * @param ml_num
     * @param principal
     * @param model
     * @return
     */
    @RequestMapping(value ="view/{ml_num:.+}", method= {RequestMethod.GET, RequestMethod.POST})
    public String view(@PathVariable int ml_num, @AuthenticationPrincipal Member principal, Model model ) {
    	// 메일 정보 가져오기
    	Mail mail = mailService.selectMail(ml_num);
    	mailService.replaceStringForHtml(mail);
    	model.addAttribute("mail", mail);
    	
    	// 받는 사람 목록 가져오기
    	List<MailTo> listOfMailTo = mailService.listMailTo(ml_num);
    	mailService.replaceStringForHtml(listOfMailTo);
    	
    	model.addAttribute("listOfMailTo", listOfMailTo);
    	
    	// 첨부파일 목록 가져오기
    	List<DriveFileInfo> listOfDriveFileInfo = mailService.listDriveFileInfo(ml_num);
    	model.addAttribute("listOfDriveFileInfo", listOfDriveFileInfo);
    	
    	// 메일 읽을 권한 있는지 체크
    	boolean isAuthorized = false;
    	for (MailTo mailTo : listOfMailTo) {
    		if (mailTo.getMl_email().toLowerCase().contains((principal.getM_id() + "@" + GlobalsOfMail.MAIL_DOMAIN).toLowerCase())) {
    			isAuthorized = true;
    			break;
    		}
    	}
    	if (!isAuthorized) {
    		return "login/denied";
    	}
    	
    	// 메일 읽은 여부 처리하기
    	mailService.updateMailRead(ml_num);
    	
    	return "mail/mailView";
    }
    
    /**
     * 메일 작성 페이지 이동
     * @return
     */
    @RequestMapping(value ="writeForm", method= {RequestMethod.GET, RequestMethod.POST})
    public String writeForm(@AuthenticationPrincipal Member principal, Model model) {
    	model.addAttribute("principal", principal);
    	return "mail/mailWriteForm";
    }
    
    
    
	/**
	 * 메일 보내기
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	@RequestMapping(value ="send", method= {RequestMethod.POST})
	public String mailSend(Mail mail, String addressTo, @RequestParam("multipartFile") List<MultipartFile> listOfMultipartFile, @AuthenticationPrincipal Member principal, Model model) throws IOException, Exception {		
		// 파일 서버에 올리고 db에 정보 등록하기
		List<DriveFileInfo> listOfDriveFileInfo = new ArrayList<DriveFileInfo>();
		for (MultipartFile multipartFile : listOfMultipartFile) {
			if (multipartFile.getSize() > 0) {
				// 파일 서버에 업로드
				String uploadPath = WebMvcConfig.RESOURCE_PATH + "/drive";
			    String dv_id = driveService.uploadFile(multipartFile.getOriginalFilename(), multipartFile.getBytes(), uploadPath);
			    
			    // DRIVE 테이블에 파일 정보 저장 
			    DriveFileInfo driveFile = new DriveFileInfo(dv_id, principal.getM_id(), multipartFile.getOriginalFilename(), null, GlobalsOfCg_num.DRIVE_SIGN);
			    driveService.insertDriveFileInfo(driveFile);
			    
				// 리스트에 추가
			    listOfDriveFileInfo.add(driveService.selectOneDriveFileInfo(dv_id));
			}			
		}
		
		// 메일 보내기
		mailService.sendMail(principal, mail, addressTo, listOfDriveFileInfo);
		
		// 메일 정보 db에 등록하기
		mailService.insertMailSent(mail);
		mailService.insertMailTo(mail.getMl_num(), addressTo);
		if (listOfDriveFileInfo.size() > 0) {
			mailService.insertMailFile(mail.getMl_num(),listOfDriveFileInfo);
		}
		
		return "redirect:/mail";
	}
	

    @RequestMapping(value="delete", method= {RequestMethod.POST})
    @ResponseBody
    public String delete(@RequestParam(value="chkArr[]", required = false) List<Integer> listOfMl_num, @AuthenticationPrincipal Member principal) {
    	if (listOfMl_num != null && listOfMl_num.size() > 0) {
    		mailService.deleteMail(principal.getM_id(), listOfMl_num);
    	}
    	return null;
    }
    



    // 이메일 추출하기 테스트
    @RequestMapping(value="test", method= {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String test(String text) {
    	System.out.println("text: " + text);
    	Matcher matcher = Pattern.compile("\\<[^\\<\\>]+\\>").matcher(text);
    	String textMatched = new String();
    	if (matcher.find()) {
    		textMatched = text.substring(matcher.start()+1, matcher.end()-1);
    	} else {
    		textMatched = text;
    	}
    	return textMatched;
    }
}
