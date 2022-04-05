package kr.co.seoulit.erp.sys.controller;

import kr.co.seoulit.erp.sys.serviceFacade.BaseServiceFacade;
import kr.co.seoulit.erp.sys.to.SysCodeDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/sys/*")
public class CodeDetailHandler {

	@Autowired
	private BaseServiceFacade baseServiceFacade;

	@RequestMapping("/findCodeDetailList")
	public List<SysCodeDetailTo> findCodeDetailList() throws Exception {

		return baseServiceFacade.findCodeDetailList();

	}

	@RequestMapping("/findPayStepCodeDetailList/{divisionCode}")
	public List<SysCodeDetailTo> findPayStepCodeDetailList(@PathVariable String divisionCode) throws Exception {
		return baseServiceFacade.findPayStepCodeDetailList(divisionCode);

	}

	@RequestMapping("/batchDetailCode")
	public void batchDetailCode(@RequestBody List<SysCodeDetailTo> codeDetailList) throws Exception {
		// System.out.println("aaaaaaa"+codeDetailList.get(0).getDetailCodeName());
		baseServiceFacade.batchDetailCodeProcess(codeDetailList);
	}
}
