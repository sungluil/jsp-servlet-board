package dto;

public class PageVo {
	private int pageNo=1;//페이지 번호
	private int pageSize;//한페이지에 뿌려질 게시글 갯수
	private int totalCnt;//총글수
	private int blockSize;//블럭에 검색될 페이지갯수
	
	private int totalPage;
	private int startRow;
	private int endRow;

	private int startPage;
	private int endPage;
	private int prevPage;
	private int nextPage;

	public PageVo() {
		// TODO Auto-generated constructor stub
	}
	
	public PageVo(int pageNo, int pageSize,int totalCnt, int blockSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalCnt = totalCnt;
		this.blockSize = blockSize;

		pageAce();
	}
	
	public void pageAce() {
		//총페이지 수는 총게시글 갯수 나누기 페이지당 출력갯수
		totalPage=(totalCnt+pageSize-1)/pageSize;
		
		//만약에 현재페이지가 0보다작거나같고 현재페이지번호가 총페이지보다 크면 페이지번호는1
		if(pageNo < 1) pageNo=1;
		if(pageNo > totalPage) pageNo=totalPage;
		
		//게시글의 시작행은 현재페이지 -1 곱하기 페이지당출력갯수 +1
		startRow = (pageNo-1) * pageSize + 1;
		//마지막행은 페이지번호 곱하기 페이지당출력갯수
		endRow = pageNo * pageSize;
		//만약에 마지막행이 총페이지 갯수보다 많으면 마지막행은=총페이지갯수
		if(endRow>totalCnt) {
			endRow=totalCnt;
		}
		
		//블럭시작페이지는 = 현재페이지-1 나누기 출력될블럭갯수 곱하기 블럭갯수+1
		startPage = (pageNo-1) / blockSize * blockSize + 1;
		
		//블럭마지막은 블럭시작 곱하기 출력될블럭갯수
		endPage = startPage + blockSize - 1;
		
		//만약에 마지막블럭이 총페이지갯수보다 높으면 마지막블럭은 총페이지갯수
		if(endPage>totalPage) {
			endPage=totalPage;
		}
		
		prevPage=startPage-blockSize;
		nextPage=startPage+blockSize;
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	
}
