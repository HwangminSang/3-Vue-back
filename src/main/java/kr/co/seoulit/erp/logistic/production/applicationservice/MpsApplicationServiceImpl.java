package kr.co.seoulit.erp.logistic.production.applicationservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.seoulit.erp.logistic.production.dao.MpsDAO;
import kr.co.seoulit.erp.logistic.production.to.ContractDetailInMpsAvailableTO;
import kr.co.seoulit.erp.logistic.production.to.MpsTO;
import kr.co.seoulit.erp.logistic.production.to.SalesPlanInMpsAvailableTO;
import kr.co.seoulit.erp.logistic.sales.dao.ContractDetailDAO;
import kr.co.seoulit.erp.logistic.sales.dao.SalesPlanDAO;

@Component
public class MpsApplicationServiceImpl implements MpsApplicationService {

	@Autowired
	private MpsDAO mpsDAO;
	@Autowired
	private ContractDetailDAO contractDetailDAO;
	@Autowired
	private SalesPlanDAO salesPlanDAO;

	public ArrayList<MpsTO> getMpsList(String startDate, String endDate, String includeMrpApply) {

		HashMap<String, String> param = new HashMap<>();
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		param.put("includeMrpApply", includeMrpApply);

		return mpsDAO.selectMpsList(param);
	}

	public ArrayList<ContractDetailInMpsAvailableTO> getContractDetailListInMpsAvailable(String searchCondition,
			String startDate, String endDate) {

		HashMap<String, String> param = new HashMap<>();
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		param.put("searchCondition", searchCondition);

		return contractDetailDAO.selectContractDetailListInMpsAvailable(param);
	}

	public ArrayList<SalesPlanInMpsAvailableTO> getSalesPlanListInMpsAvailable(String searchCondition, String startDate,
			String endDate) {

		HashMap<String, String> param = new HashMap<>();
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		param.put("searchCondition", searchCondition);

		return salesPlanDAO.selectSalesPlanListInMpsAvailable(param);
	}

	public String getNewMpsNo(String mpsPlanDate) {

		StringBuffer newEstimateNo = null;

		List<MpsTO> mpsTOlist = mpsDAO.selectMpsCount(mpsPlanDate);
		TreeSet<Integer> intSet = new TreeSet<>();
		int i;
		for (MpsTO bean : mpsTOlist) {
			String mpsNo = bean.getMpsNo();

			// MPS ?????????????????? ????????? 2????????? ????????????
			int no = Integer.parseInt(mpsNo.substring(mpsNo.length() - 2, mpsNo.length()));

			intSet.add(no);
		}

		if (intSet.isEmpty()) {
			i = 1;
		} else {
			i = intSet.pollLast() + 1; // ?????? ?????? ?????? + 1
		}

		newEstimateNo = new StringBuffer();
		newEstimateNo.append("PS");
		newEstimateNo.append(mpsPlanDate.replace("-", ""));
		newEstimateNo.append(String.format("%02d", i));

		return newEstimateNo.toString();
	}

	public HashMap<String, Object> convertContractDetailToMps(
			ContractDetailInMpsAvailableTO contractDetailInMpsAvailableTO) {

		HashMap<String, Object> resultMap = null;

		ArrayList<MpsTO> mpsTOList = new ArrayList<>();

		MpsTO newMpsBean = null;

		// MPS ??? ????????? ???????????? Bean ??? ????????? ????????? MPS Bean ??? ??????, status : "INSERT"

		System.out.println("convertContractDetailToMps ApplicationServiceImpl??????----------------------------"
				+ contractDetailInMpsAvailableTO.getContractDetailNo());
		newMpsBean = new MpsTO();

		newMpsBean.setStatus("INSERT");

		newMpsBean.setMpsPlanClassification(contractDetailInMpsAvailableTO.getPlanClassification());
		newMpsBean.setContractDetailNo(contractDetailInMpsAvailableTO.getContractDetailNo());
		newMpsBean.setItemCode(contractDetailInMpsAvailableTO.getItemCode());
		newMpsBean.setItemName(contractDetailInMpsAvailableTO.getItemName());
		newMpsBean.setUnitOfMps(contractDetailInMpsAvailableTO.getUnitOfContract());
		newMpsBean.setMpsPlanDate(contractDetailInMpsAvailableTO.getMpsPlanDate());
		newMpsBean.setMpsPlanAmount(contractDetailInMpsAvailableTO.getProductionRequirement());
		newMpsBean.setDueDateOfMps(contractDetailInMpsAvailableTO.getDueDateOfContract());
		newMpsBean.setScheduledEndDate(contractDetailInMpsAvailableTO.getScheduledEndDate());
		newMpsBean.setDescription(contractDetailInMpsAvailableTO.getDescription());

		mpsTOList.add(newMpsBean);

		resultMap = batchMpsListProcess(mpsTOList); // batchMpsListProcess ????????? ??????

		return resultMap;

	}

	public HashMap<String, Object> convertSalesPlanToMps(
			ArrayList<SalesPlanInMpsAvailableTO> salesPlanInMpsAvailableList) {

		HashMap<String, Object> resultMap = null;

		ArrayList<MpsTO> mpsTOList = new ArrayList<>();

		MpsTO newMpsBean = null;

		// MPS ??? ????????? ???????????? TO ??? ????????? ????????? MPS TO ??? ??????, status : "INSERT"
		for (SalesPlanInMpsAvailableTO bean : salesPlanInMpsAvailableList) {

			newMpsBean = new MpsTO();

			newMpsBean.setStatus("INSERT");

			newMpsBean.setMpsPlanClassification(bean.getPlanClassification());
			newMpsBean.setSalesPlanNo(bean.getSalesPlanNo());
			newMpsBean.setItemCode(bean.getItemCode());
			newMpsBean.setItemName(bean.getItemName());
			newMpsBean.setUnitOfMps(bean.getUnitOfSales());
			newMpsBean.setMpsPlanDate(bean.getMpsPlanDate());
			newMpsBean.setMpsPlanAmount(bean.getSalesAmount());
			newMpsBean.setDueDateOfMps(bean.getDueDateOfSales());
			newMpsBean.setScheduledEndDate(bean.getScheduledEndDate());
			newMpsBean.setDescription(bean.getDescription());

			mpsTOList.add(newMpsBean);

		}

		resultMap = batchMpsListProcess(mpsTOList);

		return resultMap;
	}

	public HashMap<String, Object> batchMpsListProcess(ArrayList<MpsTO> mpsTOList) {

		HashMap<String, Object> resultMap = null;

		resultMap = new HashMap<>();
		System.out.println("application???????????? ???????????? = " + mpsTOList);
		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		for (MpsTO bean : mpsTOList) {

			String status = bean.getStatus();
			System.out.println("bean?????? ????????? status??? ?????? ::::::::::" + status);

			switch (status) {

			case "INSERT":
				// ????????? ???????????????????????? ??????
				String newMpsNo = getNewMpsNo(bean.getMpsPlanDate());
				System.out.println("newMpsNo = " + newMpsNo);

				// MPS TO ??? ????????? ???????????????????????? ??????
				bean.setMpsNo(newMpsNo);

				// MPS TO Insert
				mpsDAO.insertMps(bean);

				// ????????? ????????? MPS ????????? ArrayList ??? ??????
				insertList.add(newMpsNo);

				// MPS TO ??? ????????????????????? ????????????, ???????????? ??????????????? ?????? ????????? MPS ??????????????? 'Y' ??? ??????
				if (bean.getContractDetailNo() != null) {

					changeMpsStatusInContractDetail(bean.getContractDetailNo(), "Y");

					// MPS TO ??? ????????????????????? ????????????, ???????????? ??????????????? ?????? ????????? MPS ??????????????? 'Y' ??? ??????
				} else if (bean.getSalesPlanNo() != null) {

					changeMpsStatusInSalesPlan(bean.getSalesPlanNo(), "Y");

				}

				break;

			case "UPDATE":

				mpsDAO.updateMps(bean);

				updateList.add(bean.getMpsNo());

				break;

			case "DELETE":

				mpsDAO.deleteMps(bean);

				deleteList.add(bean.getMpsNo());

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	public void changeMpsStatusInContractDetail(String contractDetailNo, String mpsStatus) {

		HashMap<String, String> param = new HashMap<>();
		param.put("contractDetailNo", contractDetailNo);
		param.put("mpsStatus", mpsStatus);

		contractDetailDAO.changeMpsStatusOfContractDetail(param);

	}

	public void changeMpsStatusInSalesPlan(String salesPlanNo, String mpsStatus) {

		HashMap<String, String> param = new HashMap<>();
		param.put("salesPlanNo", salesPlanNo);
		param.put("mpsStatus", mpsStatus);

		salesPlanDAO.changeMpsStatusOfSalesPlan(param);

	}

}
