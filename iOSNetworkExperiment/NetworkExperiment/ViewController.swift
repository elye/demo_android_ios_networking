//
//  ViewController.swift
//  NetworkExperiment
//

import Alamofire
import SwiftyJSON
import UIKit

class ViewController: UIViewController {

  @IBOutlet var searchEdit: UITextField!
  @IBOutlet var resultLabel: UILabel!
  @IBOutlet var indicator: UIActivityIndicatorView!

  private var request: DataRequest? = nil

  @IBAction func beginSearch(_ sender: Any) {
    guard let searchText = searchEdit.text, !searchText.isEmpty else {
      return
    }

    resultLabel.text = ""
    indicator.startAnimating()
    fetchData(searchText: searchText)
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    indicator.hidesWhenStopped = true
    // Do any additional setup after loading the view.
  }

  override func viewDidDisappear(_ animated: Bool) {
    request?.cancel()
  }

  private func fetchData(searchText: String) {

    let url = "https://en.wikipedia.org/w/api.php"
    let parameters: Parameters = [
      "action": "query",
      "format": "json",
      "list": "search",
      "srsearch": searchText
    ]

    request?.cancel()
    request = Alamofire.SessionManager.default.request(url, method: .get, parameters: parameters)
      .responseData { (resData) -> Void in
        self.indicator.stopAnimating()

        switch (resData.result) {
        case let .success(data):
          let swiftyJsonVar = JSON(data)
          let countObj = swiftyJsonVar["query"]["searchinfo"]["totalhits"]
          guard let count = countObj.int else {
            self.showResult(data: "No data found")
            return
          }
          self.showResult(data: "Count is \(count)")
        case let .failure(error):
          self.showResult(data: error.localizedDescription)
        }
    }
  }

  private func showResult(data: String) {
    resultLabel.text = data
  }
}
