package dev.omar.goterminal.utils;

import android.system.ErrnoException;
import android.system.Os;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ResourceUtils;

import org.jetbrains.annotations.Contract;

public final class AssetsHelper {

    @NonNull
    @Contract(pure = true)
    public static String getToolAsset(String name) {
        return "tools/" + name;
    }
    @NonNull
    @Contract(pure = true)
    public static String getScriptAsset(String name) {
        return "scripts/" + name;
    }
    @NonNull
    public static String getArchAsset(String name) {
        return ArchUtils.getArch().concat("/").concat(name);
    }
    public static boolean export(String assetName,String dest){
        return ResourceUtils.copyFileFromAssets(assetName,dest);
    }

    public static boolean exportUbuntu(){
        return exportArch("ubuntu.tar.gz",Environment.TMP_PATH.concat("/ubuntu.tar.gz"));
    }
    public static boolean exportProot(){
        boolean exported = exportArch("proot",Environment.PROOT_FILE_PATH);
        try {
            Os.chmod(Environment.PROOT_FILE_PATH,755);
            return exported;
        } catch (ErrnoException e) {
            return false;
        }
    }
    public static boolean exportBusybox(){
        boolean exported = exportTool("busybox",Environment.BUSYBOX_FILE_PATH);
        try {
            Os.chmod(Environment.BUSYBOX_FILE_PATH,755);
            return exported;
        } catch (ErrnoException e) {
            return false;
        }
    }
    public static boolean exportLibtalloc(){
        return exportArch("libtalloc.so.2",Environment.LIBTALLOC_FILE_PATH);
    }

    public static boolean exportTool(String name,String dest){
        return ResourceUtils.copyFileFromAssets(getToolAsset(name),dest);
    }

    public static boolean exportScript(String name,String dest){
        return ResourceUtils.copyFileFromAssets(getScriptAsset(name),dest);
    }

    public static boolean exportArch(String name,String dest){
        return ResourceUtils.copyFileFromAssets(getArchAsset(name),dest);
    }
}
