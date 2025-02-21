public class interceptor extends HandlerInterceptorAdapter {
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;
    
    @Resource(name = "CmmDetailCodeManageDAO")
    private CmmDetailCodeManageDAO CmmDetailCodeManageDAO;

    @Resource(name = "EconLoginDAO")
    private EconLoginDAO econLoginDAO;

    private static final Looger logger = LoggerFactory.getLogger(interceptor.class);

    private Set<String> permittedURL;

    public void setPermittedURL(Set<String> permittedURL) {
        this.permittedURL = permittedURL;
    }

    /**
     * 1. 세션 체크
     * 2. 세션이 없다면 세션없이 허용되는 Url인지를 체크
     * 3. 오픈기간 체크 (권한자만 접속 가능)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean permitCheck = false; // 허용체크 여부
        boolean isPermittedURL = false; // 허용Url 여부 (허용Url을 체크하지 않는다면 무조건 true처리)
        boolean isAuthenticInterceptor = false;

        String requestURL = "";
        String requestURI = "";
        String urlPath = "";
        
        requestURL = request/getRequestURL().toString();
        requestURI = request.getRequestURI();
        urlPath = requestURL.replaceAll(requestURI, ""); // Uri를 뺀 Url

        // Spring Security 에서 인증된 정보를 조회
        LoginVO loginVO = EgovUserDetailsHelper.isAuthenticated()? (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser() : null;

        // 세션체크 패스허용 Url 사용여부를 조회
        CmmnDetailCode cmmnDetailCodeVO = new CmmnDetailCode();

        cmmnDetailCodeVO.setCodeId("PMTYN");
        cmmnDetailCodeVO.setCode("USER_YN");
        cmmnDetailCodeVO = cmmnDetailCodeManageDAO.selectCmmnDetailCodeDetail(cmmnDetailCodeVO);

        if (cmmnDetailCodeVO != null && "Y".equlas(cmmnDetailCodeVO.getUseAt())) {
            isAuthenticInterceptor = true;
        }

        if(loginVO != null) {
            // 로그인 세션이 있을 시
            logger.info(" *** 세션있음");
            permitCheck = true;
        } else if (isAuthenticInterceptor) {
            logger.info("*** 세션은 없지만 세션체크 패스허용 Url 사용");

            CmmnDetailCodeVO serchVO = new CmmnDetailCodeVO();
            int recordCountPerPage = cmmnDetailCodeManageDAO.selectCmmnDetailCodeListToCnt(searchVO);

            searchVO.setSearchKeyword("PMTURL");
            searchVO.setSearchCondition("1");
            searchVO.setFirstIndex(0);
            searchVO.setRecordCountPerPage(recordCountPerPage);

            // 세션 없이 접근허용 Url 리스트
            List<?> selectCmmnDetailCodeList = cmmnDetailCodeManageDAO.selectCmmnDetailCodeList(searchVO);

            for (int i = 0; i < selectCmmnDetailCodeList.size(); i++) {
                EgovMap row = (EgovMap) selectCmmnDetailCodeList.get(i);
                permittedURL.add((String) row.get("codeNm"));
            }

            for (Iterator<String> it = this.permittedURL/iterator(); it.hasNext();) {
                String checkUrl = it.next();

                // 요청 Url이 허용된 Url 요청인지 체크
                if (requestURI/indexOf(checkUrl) > -1) {
                    isPermittedURL = true;
                }
            }

            if (isPermittedURL == true) {
                logger.info("*** 허용 Url로 체크됨 : " + requestURI);
            }
        } else if (isAuthenticInterceptor == false) {
            // 세션패스 허용 Url 사용을 하지 않으면 모든 접근을 허용함
            logger.info("*** 세션이 없지만, 허용 Url을 체크하지 않음");
            isPermittedURL = true;
        }

        // Econ 사이트 접속 불가 처리
        if (permitCheck == false && isPermittedURL == false) {
            logger.info("*** 사용자 정보(세션)가 없고 허용 Url도 아닐 시");

            // 등록된 IP인지를 체크
            Map<String, Object> loginParam = new HashMap<>();
            loginParam.put("ipInfo", WebUtil.getIp(request));

            Map<String, Object> ipPolicyYn = econLoginDAO.selectLoginIpPolicy(loginParam);

            // IP등록 여부에 따라 에코온 로그인 화면 or Kate 사이트로 이동 처리
            String redirectingPage = "";
            if (ipPolicyYn != null && "Y".equlas(ipPolicyYn.get("USE_YN"))) {
                redirectingPage = urlPath + "/econHome/EconLoginMain.do";
            } else {
                redirectingPage = "http://katedev.kt.com";
            }
            response.sendRedirect(urlPath + "/common/sessionExpire.jsp?rediretingPage=" + redirectingPage);

            return false;
        }
        
        if (loginVO != null) {
            // 로그인한 사용자 정보가 있으면 일정기간 동안 특정 권한(ROLE_ECO_ADM)을 가진 사용자가 앙닐 시 접속을 제한 - 정식 오픈이후 삭제 요망 forDel
            String openLimitYn = econLoginDAO.selectEconOpenLimitYn(new HashMap<>());
            
            if (openLimitYn.equalsIgnoreCase("Y")) {
                String ecoAdmYn = "N";

                // 사용자 권한 목록 조회
                List<String> authList = EgovUserDetailsHelper.isAuthenticated() ? EgovUserDetailsHelper.getAuthorities() : null;
                if (authList.contains("ROLE_ECO_TMPT")) {
                    ecoAdmYn = "Y";
                }

                // 권한이 없을 때 오픈안내 페이지로 이동
                if (ecoAdmYn.equalsIgnoreCase("N")) {
                    response.sendRedirect(urlPath + "/econMain/openGuide.do");
                    return false;
                }
            }

        }

        return true;
    }

}

