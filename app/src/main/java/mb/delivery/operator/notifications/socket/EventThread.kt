package mb.delivery.operator.notifications.socket

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class EventThread(runnable: Runnable) : Thread(runnable) {

    companion object {
        private var thread: EventThread? = null
        private var service: ExecutorService? = null
        private var counter = 0

        private val THREAD_FACTORY =
            ThreadFactory { runnable ->
                thread = EventThread(runnable)
                thread?.name = "EventThread"
                thread
            }

        private fun isCurrent(): Boolean {
            return currentThread() == thread
        }

        private fun nextTick(task: Runnable) {
            var executor: ExecutorService?
            synchronized(EventThread::class.java) {
                counter++
                if (service == null) {
                    service = Executors.newSingleThreadExecutor(THREAD_FACTORY)
                }
                executor = service
            }
            executor?.execute {
                try {
                    task.run()
                } finally {
                    synchronized(EventThread::class.java) {
                        counter--
                        if (counter == 0) {
                            service?.shutdown()
                            service = null
                            thread = null
                        }
                    }
                }
            }
        }

        fun exec(task: Runnable) {
            if (isCurrent()) {
                task.run()
            } else {
                nextTick(task)
            }
        }
    }




}