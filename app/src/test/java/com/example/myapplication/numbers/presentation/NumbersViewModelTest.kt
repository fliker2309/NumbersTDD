package com.example.myapplication.numbers.presentation

import org.junit.Assert.*
import org.junit.Test

class NumbersViewModelTest {

    /**
     * Initial test
     * At start, fetch data and show it
     * then try to get some data successfully
     * then re-init and check the result
     */

    @Test
    fun `test init and re-init`() {
        val communications = TestNumbersCommunications()
        val interactor = TestNumbersInteractor()
        // 1. initialize

        val viewModel = NumbersViewModel(communications, interactor)
        interactor.changeExpectedResult(NumbersResult.Success())
        // 2.action
        viewModel.init(isFirstRun = true)
        // 3.check
        assertEquals(1, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[0])

        assertEquals(2, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[1])

        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UiState.Success(), communications.stateCalledList[0])

        assertEquals(0, communications.numbersList.size)
        assertEquals(1, communications.timesShowList)

        // get some data
        interactor.changeExpectedResult(NumbersResult.Failure())
        viewModel.fetchRandomNumberData()

        // progress happening
        assertEquals(3, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[2])

        // interactor in progress
        assertEquals(1, interactor.fetchAboutRandomNumberCalledList.size)

        // end progress
        assertEquals(4, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[3])

        assertEquals(2, communications.stateCalledList.size)
        assertEquals(UiState.Error(/*todo error message  */), communications.stateCalledList[1])
        assertEquals(1, communications.timesShowList)

        viewModel.init(isFirstRun = false)

        // После поворота экрана сохраняется в ливдате последнее состояние, проверяем, так ли это
        assertEquals(4, communications.progressCalledList.size)
        assertEquals(2, communications.stateCalledList.size)
        assertEquals(1, communications.timesShowList)
    }

    /**
     * Try to get information about "empty" number
     */

    @Test
    fun `fact about empty number`() {
        val communications = TestNumbersCommunications()
        val interactor = TestNumbersInteractor()

        val viewModel = NumbersViewModel(communications, interactor)

        viewModel.fetchFact("")
        assertEquals(0, interactor.fetchAboutNumberCalledList.size)

        assertEquals(0, communications.progressCalledList.size)

        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UiState.Error("Entered number is empty"), communications.stateCalledList[0])

        assertEquals(0, communications.timesShowList)
    }

    /**
     * Try to get information about some number
     */

    @Test
    fun `fact about some number`() {
        val communications = TestNumbersCommunications()
        val interactor = TestNumbersInteractor()

        val viewModel = NumbersViewModel(communications, interactor)

        interactor.changeExpectedResult(NumbersResult.Success(listOf(Number("45", "fact about 45"))))
        viewModel.fetchFact("45")


        assertEquals(1, communications.progressCalledList.size)
        assertEquals(true, communications.progressCalledList[0])

        assertEquals(1, interactor.fetchAboutNumberCalledList.size)
        assertEquals(Number("45", "fact about 45"), interactor.fetchAboutNumberCalledList[0])

        assertEquals(2, communications.progressCalledList.size)
        assertEquals(false, communications.progressCalledList[1])

        assertEquals(1, communications.stateCalledList.size)
        assertEquals(UiState.Success(), communications.stateCalledList[0])

        assertEquals(1, communications.timesShowList)
        assertEquals(NumberUi("45", "fact about 45"), communications.numbersList[0])
    }

    private class TestNumbersCommunications : NumbersComminications {

        val progressCalledList = mutableListOf<Boolean>()
        val stateCalledList = mutableListOf<Boolean>()
        val numbersList = mutableListOf<NumberUi>()
        var timesShowList = 0

        override fun showProgress(show: Boolean) {
            progressCalledList.add(show)
        }

        override fun showState(state: UiState) {
            stateCalledList.add(state)
        }

        override fun showList(list: List<NumberUi>) {
            numbersList.addAll(list)
            timesShowList++
        }
    }

    private class TestNumbersInteractor() : NumbersInteractor {

        private var result: NumbersResult = NumbersResult.Success()

        val initCalledList = mutableListOf<NumbersResult>()
        val fetchAboutNumberCalledList = mutableListOf<NumbersResult>()
        val fetchAboutRandomNumberCalledList = mutableListOf<NumbersResult>()

        fun changeExpectedResult(newResult: NumbersResult) {
            result = newResult
        }

        override suspend fun init(): NumbersResult {
            initCalledList.add(result)
            return result
        }

        override suspend fun factAboutNumber(number: String): NumbersResult {
            fetchAboutNumberCalledList.add(result)
            return result
        }

        override suspend fun factAboutRandomNumber(): NumbersResult {
            fetchAboutRandomNumberCalledList.add(result)
            return result
        }
    }
}
