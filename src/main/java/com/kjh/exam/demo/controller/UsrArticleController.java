package com.kjh.exam.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kjh.exam.demo.service.ArticleService;
import com.kjh.exam.demo.service.BoardService;
import com.kjh.exam.demo.service.ReactionPointService;
import com.kjh.exam.demo.service.ReplyService;
import com.kjh.exam.demo.util.Ut;
import com.kjh.exam.demo.vo.Article;
import com.kjh.exam.demo.vo.Board;
import com.kjh.exam.demo.vo.Reply;
import com.kjh.exam.demo.vo.ResultData;
import com.kjh.exam.demo.vo.Rq;

@Controller
public class UsrArticleController {

	@Autowired
	private ArticleService articleService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private ReactionPointService reactionPointService;
	@Autowired
	private Rq rq;

	@RequestMapping("/usr/article/detail")
	public String showDetail(Model model, int id) {
			
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		
		ResultData actorCanMakeReactionRd = reactionPointService.actorCanMakeReaction(rq.getLoginedMemberId(),"article", id);

		model.addAttribute("article", article);
		model.addAttribute("actorCanMakeReactionRd", actorCanMakeReactionRd);
		model.addAttribute("actorCanMakeReaction", actorCanMakeReactionRd.isSuccess());
		
		List<Reply> replies = replyService.getForPrintReplies("article",id);
		model.addAttribute("replies",replies);
		
		if (actorCanMakeReactionRd.getResultCode().equals("F-2")) {
			int sumReactionPointByMemberId = (int) actorCanMakeReactionRd.getData1();

			if (sumReactionPointByMemberId > 0) {
				model.addAttribute("actorCanCancelGoodReaction", true);
			} else {
				model.addAttribute("actorCanCancelBadReaction", true);
			}
		}
		return "usr/article/detail";
	}

	@RequestMapping("/usr/article/list")
	public String showList(Model model, @RequestParam(defaultValue = "1") int boardId,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "title,body") String searchKeywordType,
			@RequestParam(defaultValue = "") String searchKeyword) {
		Board board = boardService.getBoardById(boardId);

		if (board == null) {
			return rq.jsHistoryBackOnView("???????????? ?????? ????????? ?????????.");
		}

		int itemsInAPage = 10;

		List<Article> articles = articleService.getForPrintArticles(rq.getLoginedMemberId(), boardId, page,
				itemsInAPage, searchKeywordType, searchKeyword);

		int articlesCount = articleService.getArticlesCount(boardId, searchKeywordType, searchKeyword);
		int pagesCount = (int) Math.ceil((double) articlesCount / itemsInAPage);
		model.addAttribute("articles", articles);
		model.addAttribute("board", board);
		model.addAttribute("articlesCount", articlesCount);
		model.addAttribute("page", page);
		model.addAttribute("pagesCount", pagesCount);
		return "usr/article/list";
	}

	@RequestMapping("/usr/article/write")
	public String showWrite() {
		return "usr/article/write";
	}

	@RequestMapping("/usr/article/doWrite")
	@ResponseBody
	public String doWrite(String title, String body, int boardId) {

		if (Ut.empty(title)) {
			return rq.jsHistoryBack("????????? ????????? ?????????.");
		}
		if (Ut.empty(body)) {
			return rq.jsHistoryBack("????????? ????????? ?????????.");
		}
		ResultData<Integer> writeRd = articleService.writeArticle(rq.getLoginedMemberId(), title, body, boardId);

		int id = (int) writeRd.getData1();

		return rq.jsReplace(Ut.f("%d??? ???????????? ?????? ???????????????.", id), Ut.f("../article/detail?id=%d", id));
	}

	@RequestMapping("/usr/article/modify")
	public String showModify(Model model, int id) {
		
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		model.addAttribute("article", article);

		if (article == null) {
			return rq.jsHistoryBackOnView(Ut.f("%d??? ???????????? ???????????? ????????????.", id));
		}

		ResultData actorCanModifyRd = articleService.actorCanModify(rq.getLoginedMemberId(), article);

		if (actorCanModifyRd.isFail()) {
			return rq.jsHistoryBackOnView(actorCanModifyRd.getMsg());
		}
		
		return "usr/article/modify";
	}

	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public String doModify(int id, String title, String body) {

		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);

		if (article == null) {
			return rq.jsHistoryBack(Ut.f("%d??? ???????????? ???????????? ????????????.", id));
		}

		ResultData actorCanModifyRd = articleService.actorCanModify(rq.getLoginedMemberId(), article);
		if (actorCanModifyRd.isFail()) {
			return rq.jsHistoryBack(actorCanModifyRd.getMsg());
		}

		articleService.modifyArticle(id, title, body);
		return rq.jsReplace(Ut.f("%d??? ????????? ??????", id), Ut.f("../article/detail?id=%d", id));
	}

	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public String doDelete(int id) {

		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);

		if (article == null) {
			return rq.jsHistoryBack(Ut.f("%d??? ???????????? ???????????? ????????????.", id));
		}
		if (rq.getLoginedMemberId() != article.getMemberId()) {
			return rq.jsHistoryBack("?????? ???????????? ?????? ?????? ????????? ????????????.");
		}

		articleService.deleteArticle(id);

		return rq.jsReplace(Ut.f("%d??? ???????????? ?????? ????????????.", id), Ut.f("../article/list?boardId=%d", article.getBoardId()));
	}
	
	@RequestMapping("/usr/article/doIncreaseHitCountRd")
	@ResponseBody
	public ResultData<Integer> doIncreaseHitCount(int id){
		ResultData<Integer> increaseHitCountRd = articleService.increaseHitCount(id);
		if(increaseHitCountRd.isFail()) {
			return increaseHitCountRd;
		}
		int hitCount = articleService.getHitCount(id);
		ResultData<Integer> rd = ResultData.newData(increaseHitCountRd, "hitCount", hitCount);
		rd.setData2("id",id);
		return rd;
	}

}