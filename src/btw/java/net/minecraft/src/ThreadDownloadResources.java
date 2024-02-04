package net.minecraft.src;

import argo.jdom.*;
import argo.saj.InvalidSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ThreadDownloadResources extends Thread
{
    private static JdomParser parser = new JdomParser();

    /** The folder to store the resources in. */
    public File resourcesFolder;

    /** A reference to the Minecraft object. */
    private Minecraft mc;

    /** Set to true when Minecraft is closing down. */
    private boolean closing = false;

    public ThreadDownloadResources(File file, Minecraft mc)
    {
        this.mc = mc;
        this.setName("Resource download thread");
        this.setDaemon(true);
        this.resourcesFolder = new File(file, "resources/");

        if (!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
        }
    }

    public JsonRootNode fetchJson(URL url) throws IOException {
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.connect();

        JsonRootNode json = null;

        if (connection.getResponseCode() / 100 == 4) {
            return null;
        }

        try {
            json = ThreadDownloadResources.parser.parse(new InputStreamReader(connection.getInputStream()));
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return json;
    }

    public void run()
    {
        try
        {
            URL versionManifestURL = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            URL versionURL = null;
            URL assetIndexURL = null;
            URL assetURL = new URL("https://resources.download.minecraft.net/");

            JsonRootNode versionManifest = fetchJson(versionManifestURL);

            List<JsonNode> versions = versionManifest.getArrayNode("versions");

            for(int index = 0; index < versions.size(); index++) {
                JsonNode versionMeta = versions.get(index);

                if(versionMeta.getStringValue("id").equalsIgnoreCase("1.5.2")) {
                    versionURL = new URL(versionMeta.getStringValue("url"));
                }
            }

            JsonNode version = fetchJson(versionURL);

            assetIndexURL = new URL(version.getStringValue("assetIndex", "url"));

            JsonRootNode assetIndex = fetchJson(assetIndexURL);

            JsonNode objects = (JsonNode) assetIndex.getFields().get(JsonNodeFactories.aJsonString("objects"));

            Iterator<JsonStringNode> iterator = objects.getFields().keySet().iterator();

            while(iterator.hasNext()) {
                String path = iterator.next().getText();

                String hash = objects.getStringValue(path, "hash");
                long size = Integer.parseInt(objects.getNumberValue(path, "size"));

                if (size > 0L) {
                    this.downloadAndInstallResource(assetURL, path, hash, size);

                    if (this.closing) {
                        return;
                    }
                }
            }
        }
        catch (Exception e)
        {
            this.loadResource(this.resourcesFolder, "");
            e.printStackTrace();
        }
    }

    /**
     * Reloads the resource folder and passes the resources to Minecraft to install.
     */
    public void reloadResources()
    {
        this.loadResource(this.resourcesFolder, "");
    }

    /**
     * Loads a resource and passes it to Minecraft to install.
     */
    private void loadResource(File file, String path)
    {
        File[] files = file.listFiles();

        for (int fileIndex = 0; fileIndex < files.length; ++fileIndex)
        {
            if (files[fileIndex].isDirectory())
            {
                this.loadResource(files[fileIndex], path + files[fileIndex].getName() + "/");
            }
            else
            {
                try
                {
                    this.mc.installResource(path + files[fileIndex].getName(), files[fileIndex]);
                }
                catch (Exception e)
                {
                    this.mc.getLogAgent().logWarning("Failed to add " + path + files[fileIndex].getName() + " in resources");
                }
            }
        }
    }

    /**
     * Downloads the resource and saves it to disk then installs it.
     */
    private void downloadAndInstallResource(URL url, String path, String hash, long fileSize)
    {
        try
        {
            File file = new File(this.resourcesFolder, path);

            if (!file.exists() || file.length() != fileSize)
            {
                file.getParentFile().mkdirs();
                String assetHash = hash.substring(0, 2) + "/" + hash;
                this.downloadResource(new URL(url, assetHash), file);

                if (this.closing)
                {
                    return;
                }
            }

            if(path.indexOf("/") >= 0) {
                this.mc.installResource(path, file);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the resource and saves it to disk.
     */
    private void downloadResource(URL url, File file) throws IOException
    {
        byte[] buffer = new byte[4096];
        DataInputStream inputStream = new DataInputStream(url.openStream());
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));

        do
        {
            int data;

            if ((data = inputStream.read(buffer)) < 0)
            {
                inputStream.close();
                outputStream.close();
                return;
            }

            outputStream.write(buffer, 0, data);
        }
        while (!this.closing);
    }

    /**
     * Called when Minecraft is closing down.
     */
    public void closeMinecraft()
    {
        this.closing = true;
    }
}
