package com.stratagile.pnrouter.data.fileInfo;

import android.support.annotation.NonNull;
import android.widget.Toast;


import com.stratagile.pnrouter.R;

import java.io.File;
import java.util.List;

import io.julian.common.Preconditions;
import io.julian.mvp.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

//import rx.Observable;
//import rx.functions.Func1;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:00
 */

public class FileInfosRepository implements FileInfosDataSource {

    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;

    public FileInfosRepository() {
//        mSchedulerProvider = PreconditionsLocal.checkNotNull(schedulerProvider, "schedulerProvider == null");
    }

//    .map(new Func1<FileInfo, File>() {
//        @Override
//        public File call(FileInfo fileInfo) {
//            return new File(fileInfo.getAbsolutePath());
//        }
//    })

    @Override
    public Observable<List<FileInfo>> listFileInfos(@NonNull FileInfo directory) {
        Preconditions.checkNotNull(directory);
        Preconditions.checkArgument(directory.isDirectory());
        return Observable.just(directory)
                .subscribeOn(Schedulers.io())
                .map(new Function<FileInfo, File>() {
                    @Override
                    public File apply(FileInfo fileInfo) throws Exception {
                        return new File(fileInfo.getAbsolutePath());
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) throws Exception {
                        File[] files = file.listFiles();
                        if (files.length == 0) {
                            File file1 = new File("");
                            files = new File[] {file1};
                        }
                        if (files == null) {
                            files = new File[1];
                        }
                        return Observable.fromArray(files);
                    }
                })
                .map(new Function<File, FileInfo>() {
                    @Override
                    public FileInfo apply(File file) throws Exception {
                        return new FileInfo(file);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Toast.makeText(getContext(), getContext().getString(R.string.error), Toast.LENGTH_LONG);
                    }
                })
                .buffer(1000);
    }
}
